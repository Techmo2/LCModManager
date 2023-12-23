package brad.tillmann;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class LethalCompanyModVersion {
    /* Versions example:
        {
        "name":"r2modman",
        "full_name":"ebkr-r2modman-3.1.45",
        "description":"A simple and easy to use mod manager for several games using Thunderstore",
        "icon":"https://gcdn.thunderstore.io/live/repository/icons/ebkr-r2modman-3.1.45.png",
        "version_number":"3.1.45",
        "dependencies":[],
        "download_url":"https://thunderstore.io/package/download/ebkr/r2modman/3.1.45/",
        "downloads":695802,
        "date_created":"2023-11-24T17:45:34.982794Z",
        "website_url":"https://github.com/ebkr/r2modmanPlus",
        "is_active":true,"uuid4":"25b7b6f3-6804-4d3a-8b94-9c31f99d678a",
        "file_size":199387810}
        */
    private String name;
    @JsonAlias({"full_name"})
    private String fullName;
    private String description;
    @JsonAlias({"version_number"})
    private String versionNumber;
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

    public LethalCompanyModVersion()
    {

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

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
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
}
