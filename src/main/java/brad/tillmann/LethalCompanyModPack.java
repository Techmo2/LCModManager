package brad.tillmann;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LethalCompanyModPack {
    private String name;
    private String author;
    private String version;
    private Timestamp created;
    private Timestamp modified;
    private List<LethalCompanyMod> modDescriptors;
    private Map<String, LethalCompanyModVersion> modVersionMap;

    public static LethalCompanyModPack fromFile(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(file, LethalCompanyModPack.class);
    }

    public LethalCompanyModPack()
    {
        this.name = "New Mod Pack";
        this.author = "";
        this.version = "1.0.0";
        this.created = new Timestamp(System.currentTimeMillis());
        this.modified = new Timestamp(System.currentTimeMillis());
        this.modDescriptors = new ArrayList<>();
        this.modVersionMap = new HashMap<>();
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

    public Map<String, LethalCompanyModVersion> getModVersionMap() {
        return modVersionMap;
    }

    public void setModVersionMap(Map<String, LethalCompanyModVersion> modVersionMap) {
        this.modVersionMap = modVersionMap;
    }

    public LethalCompanyModVersion getModVersion(String uuid)
    {
        return modVersionMap.get(uuid);
    }

    public void setModVersion(String uuid, LethalCompanyModVersion modVersion)
    {
        if(modVersionMap.containsKey(uuid))
            modVersionMap.replace(uuid, modVersion);
        else
            modVersionMap.put(uuid, modVersion);
    }

    public List<LethalCompanyMod> getModDescriptors()
    {
        return modDescriptors;
    }

    public void setModDescriptors(List<LethalCompanyMod> modDescriptors)
    {
        this.modDescriptors = modDescriptors;
    }

    public void toFile(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        objectWriter.writeValue(file, this);
    }
}
