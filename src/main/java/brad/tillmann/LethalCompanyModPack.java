package brad.tillmann;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LethalCompanyModPack {
    private String name;
    private String author;
    private String version;
    private Timestamp created;
    private Timestamp modified;
    private List<LethalCompanyModVersion> modVersions;

    public static LethalCompanyModPack fromFile(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        File file = new File(path);
        return objectMapper.readValue(file, LethalCompanyModPack.class);
    }

    public LethalCompanyModPack(String author)
    {
        this.name = "New Mod Pack";
        this.author = author;
        this.version = "1.0.0";
        this.created = new Timestamp(System.currentTimeMillis());
        this.modified = new Timestamp(System.currentTimeMillis());
        this.modVersions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public List<LethalCompanyModVersion> getModVersions() {
        return modVersions;
    }

    public void setModVersions(List<LethalCompanyModVersion> modVersions) {
        this.modVersions = modVersions;
    }

    public void toFile(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        objectWriter.writeValue(new File(path), this);
    }
}
