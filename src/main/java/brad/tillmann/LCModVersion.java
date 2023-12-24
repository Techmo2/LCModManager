package brad.tillmann;

import com.fasterxml.jackson.annotation.JsonAlias;
import de.skuzzle.semantic.Version;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;


public class LCModVersion {
    private String name;
    @JsonAlias({"full_name"})
    private String fullName;
    private String description;
    @JsonAlias({"version_number"})
    //@JsonDeserialize(converter = StringToVersionConverter.class)
    private Version version;
    private List<Object> dependencies; // TODO: Figure out what type this should be
    @JsonAlias({"download_url"})
    private String downloadUrl;
    private Long downloads;
    @JsonAlias({"date_created"})
    private String dateCreated;
    @JsonAlias({"website_url"})
    private String websiteUrl;
    @JsonAlias({"is_active"})
    private boolean isActive;
    @JsonAlias({"uuid4"})
    private String uuid;
    @JsonAlias({"file_size"})
    private Long fileSize;

    public LCModVersion() {

    }

    private static void unzipUrlToPath(final URL url, final Path destination) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Channels.newInputStream(Channels.newChannel(url.openStream())))) {
            for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry()) {
                Path toPath = destination.resolve(entry.getName());
                if (!toPath.startsWith(destination)) {
                    // see: https://snyk.io/research/zip-slip-vulnerability
                    throw new RuntimeException("Entry with an illegal path: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectory(toPath);
                } else try (FileChannel fileChannel = FileChannel.open(toPath, WRITE, CREATE/*, DELETE_ON_CLOSE*/)) {
                    fileChannel.transferFrom(Channels.newChannel(zipInputStream), 0, Long.MAX_VALUE);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setVersion(String versionString) {
        this.version = Version.parseVersion(versionString);
    }

    public List<Object> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Object> dependencies) {
        this.dependencies = dependencies;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Path getDownloadDirectory() {
        return LCModManager.getModManagerDownloadDirectory().resolve(fullName).resolve(version.toString());
    }

    public void download() throws IOException {
        if (!Files.exists(getDownloadDirectory())) {
            // We will download the archive, then extract it to ${MOD_FULL_NAME}/${MOD_VERSION}
            // Ensure destination exists
            Path destination = getDownloadDirectory();
            System.out.printf("Downloading files for %s to %s%n", fullName, destination);
            Files.createDirectories(destination);

            // Download and extract mod files
            unzipUrlToPath(new URL(downloadUrl), destination);
        } else {
            System.out.printf("Mod files for %s version %s already exist. Skipping download.%n", fullName, version);
        }
    }

    public void install() {
        try {
            if (!Files.exists(getDownloadDirectory()))
                download();

            Path from = getDownloadDirectory().resolve("BepInEx");
            if (!Files.exists(from))
                throw new FileNotFoundException("Could not find BepInEx folder for mod");

            // Do the thing, and write a journal so we know how we modified the files (so we can uninstall)
            // TODO: Keep a copy of the replaced files as well for uninstallation
            copyFolder(from, LCModManager.getLethalCompanyInstallationDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uninstall() {

    }

    private void copyFolder(Path src, Path dest) throws IOException {
        LCModVersionInstallationJournal journal = LCModVersionInstallationJournal.openNew(this);
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> {
                Path to = dest.resolve(src.relativize(source));
                if (Files.exists(to))
                    journal.addEntry(source, to, LCModVersionInstallationJournal.FileOperationJournalEntry.REPLACE);
                else
                    journal.addEntry(source, to, LCModVersionInstallationJournal.FileOperationJournalEntry.COPY);
                copy(source, to);
            });
        }

        // If a journal for this mod version already exists, and the new journal only contains "REPLACE", keep the old journal
        // It's likely the mod was already installed, and was just re-installed.
        LCModVersionInstallationJournal existingJournal = LCModVersionInstallationJournal.openExisting(this);
        boolean reinstalled = !existingJournal.getEntries().isEmpty() && journal.getEntries().stream().filter(entry -> !entry.getRight().equals(LCModVersionInstallationJournal.FileOperationJournalEntry.REPLACE)).count() == 0;
        if (!reinstalled) {
            // Write journal if this is a new install
            journal.close();
        }
    }

    private void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
