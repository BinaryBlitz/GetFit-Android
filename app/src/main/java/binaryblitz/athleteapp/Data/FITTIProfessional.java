package binaryblitz.athleteapp.Data;

public class FITTIProfessional {
    private String id;

    private String photoUrl;
    private String userPhotoUrl;

    @SuppressWarnings("unused")
    private int photoResId;
    @SuppressWarnings("unused")
    private int debug_userPhotoResId;

    private String name;
    private String desc;
    private FITTIProfessionalType type;

    private boolean following;
    private int programCount;

    private int userCount;
    private double starCount;

    public FITTIProfessional(String id, String photoUrl, String userPhotoUrl,
                             int photoResId, int debug_userPhotoResId, String name,
                             String desc, FITTIProfessionalType type, boolean following,
                             int programCount, int userCount, double starCount) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.userPhotoUrl = userPhotoUrl;
        this.photoResId = photoResId;
        this.debug_userPhotoResId = debug_userPhotoResId;
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.following = following;
        this.programCount = programCount;
        this.userCount = userCount;
        this.starCount = starCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public int getPhotoResId() {
        return photoResId;
    }

    public void setPhotoResId(int photoResId) {
        this.photoResId = photoResId;
    }

    public int getDebug_userPhotoResId() {
        return debug_userPhotoResId;
    }

    public void setDebug_userPhotoResId(int debug_userPhotoResId) {
        this.debug_userPhotoResId = debug_userPhotoResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public FITTIProfessionalType getType() {
        return type;
    }

    public void setType(FITTIProfessionalType type) {
        this.type = type;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public int getProgramCount() {
        return programCount;
    }

    public void setProgramCount(int programCount) {
        this.programCount = programCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public double getStarCount() {
        return starCount;
    }

    public void setStarCount(double starCount) {
        this.starCount = starCount;
    }
}
