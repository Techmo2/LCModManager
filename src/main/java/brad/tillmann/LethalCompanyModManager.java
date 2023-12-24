package brad.tillmann;

import net.harawata.appdirs.AppDirsFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import de.skuzzle.semantic.Version;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * This class manages the local installation of mods
 */
public class LethalCompanyModManager {
    private static class InitializationOnDemandClassHolder
    {
        private static final LethalCompanyModManager instance = new LethalCompanyModManager();
    }

    public static LethalCompanyModManager getInstance()
    {
        return LethalCompanyModManager.InitializationOnDemandClassHolder.instance;
    }

    /**
     * Create a new mod manager (autodetect lethal company installation path)
     */
    private LethalCompanyModManager() {
        try {
            Files.createDirectories(getModManagerDataDirectory());

            // Get lethal company installation directory & check it exists
            if (!Files.exists(getLethalCompanyInstallationDirectory()) || !Files.exists(getLethalCompanyExecutablePath()))
                throw new IOException(String.format("No Lethal Company installation found in directory %s. Please ensure the game is installed or try manually specifying the game installation directory.", getLethalCompanyInstallationDirectory()));

        } catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public void installModPack(LethalCompanyModPack modPack) throws Exception {
        for(LethalCompanyMod mod: modPack.getModDescriptors())
        {
            LethalCompanyModVersion modVersion = modPack.getModVersion(mod.getUuid());
            modVersion.install();
        }
    }

    public void uninstallModPack(LethalCompanyModPack modPack)
    {
        for(LethalCompanyMod mod: modPack.getModDescriptors())
        {
            LethalCompanyModVersion modVersion = modPack.getModVersion(mod.getUuid());
            modVersion.uninstall();
        }
    }

    public static Path getLethalCompanyInstallationDirectory()
    {
        return Paths.get("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Lethal Company");
    }

    public static Path getLethalCompanyExecutablePath()
    {
        return getLethalCompanyInstallationDirectory().resolve("Lethal Company.exe");
    }

    public static Path getModManagerDataDirectory()
    {
        return Paths.get(AppDirsFactory.getInstance().getSiteDataDir(LethalCompanyModManager.class.getPackage().getImplementationTitle(), null, null));
    }

    public static Path getModManagerConfigDirectory()
    {
        return Paths.get(AppDirsFactory.getInstance().getSiteConfigDir(LethalCompanyModManager.class.getPackage().getImplementationTitle(), null, null));
    }

    public static Path getModManagerDownloadDirectory()
    {
        return getModManagerDataDirectory().resolve("downloads");
    }

    public static Path getJournalDirectory()
    {
        return getModManagerDataDirectory().resolve("journal");
    }

    private LethalCompanyModVersion selectModVersion(LethalCompanyMod descriptor, Version version)
    {
        if(version == null && !descriptor.getVersions().isEmpty())
        {
            // Use newest version
            List<LethalCompanyModVersion> modVersions = descriptor.getVersions();
            modVersions.sort(Comparator.comparing(LethalCompanyModVersion::getVersion));
            Collections.reverse(modVersions);
            return modVersions.get(0);
        }

        return descriptor.getVersions().stream()
                .filter(v -> {
                    assert version != null;
                    return version.equals(v.getVersion());
                })
                .findFirst()
                .orElse(null);
    }
}
