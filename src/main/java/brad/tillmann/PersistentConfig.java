package brad.tillmann;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PersistentConfig {
    private static final Path persistentFileDirectory = Paths.get(AppDirsFactory.getInstance().getSiteConfigDir(LCModManager.class.getPackage().getImplementationTitle(), null, null));
    private static final Path persistentFilePath = persistentFileDirectory.resolve("persist.json");
    private final ObjectMapper objectMapper;
    private Map<String, Object> config;

    private PersistentConfig() {
        objectMapper = new ObjectMapper();
        config = Collections.emptyMap();

        // Create config file if it doesn't exist
        try {
            loadFromFile();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static PersistentConfig getInstance() {
        return PersistentConfig.InitializationOnDemandClassHolder.instance;
    }

    public Object getValue(String key) {
        return config.get(key);
    }

    public void setValue(String key, Object value) {
        config.put(key, value);
    }

    public void flush() {
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() throws IOException {
        if (!Files.exists(persistentFileDirectory))
            Files.createDirectories(persistentFileDirectory);

        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        File file = new File(persistentFilePath.toUri());
        objectWriter.writeValue(file, config);
    }

    private void loadFromFile() throws IOException {
        if (!Files.exists(persistentFilePath)) {
            saveToFile();
            config = new HashMap<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        config = objectMapper.readValue(new File(persistentFilePath.toUri()), Map.class);
    }

    private static class InitializationOnDemandClassHolder {
        private static final PersistentConfig instance = new PersistentConfig();

    }
}
