package brad.tillmann;

import net.harawata.appdirs.AppDirsFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * This class manages the local installation of mods
 */
public class LethalCompanyModManager {

    private static final Path lethalCompanyInstallationPath = Paths.get("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Lethal Company");
    private static final Path lethalCompanyExecutablePath = lethalCompanyInstallationPath.resolve("Lethal Company.exe");
    private static final Path modManagerDataPath = Paths.get(AppDirsFactory.getInstance().getSiteDataDir(LethalCompanyModManager.class.getPackage().getImplementationTitle(), null, null));

    /**
     * Create a new mod manager (autodetect lethal company installation path)
     */
    public LethalCompanyModManager() throws IOException {
        Files.createDirectories(modManagerDataPath);

        // Get lethal company installation directory & check it exists
        if(!Files.exists(lethalCompanyInstallationPath) || !Files.exists(lethalCompanyExecutablePath))
            throw new IOException(String.format("No Lethal Company installation found in directory %s. Please ensure the game is installed or try manually specifying the game installation directory.", lethalCompanyInstallationPath));
    }

    /**
     * Downloads the contents of a given mod and version to the mod manager's data directory.
     * This does not install the mod, but simply makes it available for installation.
     * @param descriptor
     * @param versionNumber
     * @throws Exception
     */
    public void downloadModFiles(LethalCompanyModDescriptor descriptor, String versionNumber) throws Exception {
        LethalCompanyModVersion modVersion = descriptor.getVersions().stream().filter(v -> versionNumber.equals(v.getVersionNumber())).findFirst().orElse(null);
        if(modVersion == null)
            throw new Exception(String.format("No version number matching %s exists for lethal company mod %s", versionNumber, descriptor.getName()));

        // We will download the archive, then extract it to ${MOD_OWNER}-${MOD_NAME}/${MOD_VERSION}
        // Ensure destination exists
        Path modFilesDestination = modManagerDataPath.resolve("archives").resolve(descriptor.getOwner() + "-" + descriptor.getName()).resolve(modVersion.getVersionNumber());
        Files.createDirectories(modFilesDestination);

        // Download and extract mod files
        unzipUrlToPath(new URL(modVersion.getDownloadUrl()), modFilesDestination);
    }

    public static void unzipUrlToPath(final URL url, final Path destination) throws IOException {
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

    private void installBepInEx()
    {
        // TODO: Add BepInEx to game folder
    }

    private void uninstallBepInEx()
    {
        // TODO: Remove BepInEx from game folder
    }

    private boolean isBepInExInstalled()
    {
        // TODO: Check for BepInEx in game folder
        return false;
    }

    private void installMod(LethalCompanyModDescriptor modDescriptor, LethalCompanyModVersion version)
    {
        // TODO: Download the given mod version and install to the game folder
    }

    private void uninstallAllMods()
    {
        // TODO: Uninstall all mods from the game folder (leaving BepInEx installed)
    }
}
