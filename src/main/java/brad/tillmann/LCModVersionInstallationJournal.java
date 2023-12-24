package brad.tillmann;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LCModVersionInstallationJournal {

    public enum FileOperationJournalEntry
    {
        COPY,
        REPLACE,
        DELETE
    }

    public static LCModVersionInstallationJournal openExisting(LethalCompanyModVersion modVersion)
    {
        LCModVersionInstallationJournal installationJournal = new LCModVersionInstallationJournal(modVersion);

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            installationJournal.journal = objectMapper.readValue(new File(getJournalFilePath(modVersion).toUri()), new TypeReference<List<Triple<Path, Path, FileOperationJournalEntry>>>(){});
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return installationJournal;
    }

    public static LCModVersionInstallationJournal openNew(LethalCompanyModVersion modVersion)
    {
        LCModVersionInstallationJournal installationJournal = new LCModVersionInstallationJournal(modVersion);
        return installationJournal;
    }

    public static boolean exists(LethalCompanyModVersion modVersion)
    {
        return Files.exists(getJournalFilePath(modVersion));
    }

    private LethalCompanyModVersion modVersion;
    private List<Triple<Path, Path, FileOperationJournalEntry>> journal;

    private LCModVersionInstallationJournal(LethalCompanyModVersion modVersion)
    {
        this.modVersion = modVersion;
        journal = new ArrayList<>();
    }

    public void addEntry(Path src, Path dest, FileOperationJournalEntry operation)
    {
        journal.add(Triple.of(src, dest, operation));
    }

    public List<Triple<Path, Path, FileOperationJournalEntry>> getEntries()
    {
        return journal;
    }

    public void close()
    {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
            objectWriter.writeValue(new File(getJournalFilePath(modVersion).toUri()), journal);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static Path getJournalFilePath(LethalCompanyModVersion modVersion)
    {
        return LethalCompanyModManager.getJournalDirectory().resolve(modVersion.getFullName()).resolve(modVersion.getVersion().toString()).resolve("journal.json");
    }
}
