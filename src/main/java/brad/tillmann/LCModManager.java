package brad.tillmann;

import de.skuzzle.semantic.Version;
import net.harawata.appdirs.AppDirsFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class manages the local installation of mods
 */
public class LCModManager {
    /**
     * Create a new mod manager (autodetect lethal company installation path)
     */
    private LCModManager() {
        try {
            Files.createDirectories(getModManagerDataDirectory());

            // Get lethal company installation directory & check it exists
            if (!Files.exists(getLethalCompanyInstallationDirectory()) || !Files.exists(getLethalCompanyExecutablePath()))
                throw new IOException(String.format("No Lethal Company installation found in directory %s. Please ensure the game is installed or try manually specifying the game installation directory.", getLethalCompanyInstallationDirectory()));

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public static LCModManager getInstance() {
        return LCModManager.InitializationOnDemandClassHolder.instance;
    }

    public static Path getLethalCompanyInstallationDirectory() {
        return Paths.get("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Lethal Company");
    }

    public static Path getLethalCompanyExecutablePath() {
        return getLethalCompanyInstallationDirectory().resolve("Lethal Company.exe");
    }

    public static Path getModManagerDataDirectory() {
        return Paths.get(AppDirsFactory.getInstance().getSiteDataDir(LCModManager.class.getPackage().getImplementationTitle(), null, null));
    }

    public static Path getModManagerConfigDirectory() {
        return Paths.get(AppDirsFactory.getInstance().getSiteConfigDir(LCModManager.class.getPackage().getImplementationTitle(), null, null));
    }

    public static Path getModManagerDownloadDirectory() {
        return getModManagerDataDirectory().resolve("downloads");
    }

    public static Path getJournalDirectory() {
        return getModManagerDataDirectory().resolve("journal");
    }

    public void installModPack(LCModPack modPack) {
        for (LCMod mod : modPack.getModDescriptors()) {
            LCModVersion modVersion = modPack.getModVersion(mod.getUuid());
            modVersion.install();
        }
    }

    public void uninstallModPack(LCModPack modPack) {
        for (LCMod mod : modPack.getModDescriptors()) {
            LCModVersion modVersion = modPack.getModVersion(mod.getUuid());
            modVersion.uninstall();
        }
    }

    private LCModVersion selectModVersion(LCMod descriptor, Version version) {
        if (version == null && !descriptor.getVersions().isEmpty()) {
            // Use newest version
            List<LCModVersion> modVersions = descriptor.getVersions();
            modVersions.sort(Comparator.comparing(LCModVersion::getVersion));
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

    private static class InitializationOnDemandClassHolder {
        private static final LCModManager instance = new LCModManager();
    }
}
