package brad.tillmann;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class LCMod {
    private String name;
    @JsonAlias({"full_name"})
    private String fullName;
    private String owner;
    @JsonAlias({"package_url"})
    private String packageUrl;
    @JsonAlias({"donation_link"})
    private String donationLink;
    @JsonAlias({"date_created"})
    private String dateCreated;
    @JsonAlias({"date_updated"})
    private String dateUpdated;
    @JsonAlias({"uuid4"})
    private String uuid;
    @JsonAlias({"rating_score"})
    private Long ratingScore;
    @JsonAlias({"is_pinned"})
    private boolean isPinned;
    @JsonAlias({"is_depreciated"})
    private boolean isDepreciated;
    @JsonAlias({"has_nsfw_content"})
    private boolean hasNsfwContent;
    private List<String> categories;
    private List<LCModVersion> versions; // Map of mod versions by version number

    //private Map<String, Object> properties;

    public LCMod()
    {

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public String getDonationLink() {
        return donationLink;
    }

    public void setDonationLink(String donationLink) {
        this.donationLink = donationLink;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Long ratingScore) {
        this.ratingScore = ratingScore;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isDepreciated() {
        return isDepreciated;
    }

    public void setDepreciated(boolean depreciated) {
        isDepreciated = depreciated;
    }

    public boolean isHasNsfwContent() {
        return hasNsfwContent;
    }

    public void setHasNsfwContent(boolean hasNsfwContent) {
        this.hasNsfwContent = hasNsfwContent;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<LCModVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<LCModVersion> versions) {
        this.versions = versions;
    }

    public void download()
    {

    }
}
