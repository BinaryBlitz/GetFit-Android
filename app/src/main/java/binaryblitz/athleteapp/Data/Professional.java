package binaryblitz.athleteapp.Data;

public class Professional {
    private String id;

    private String photoUrl;
    private String userPhotoUrl;

    private String name;
    private String desc;
    private ProfessionalType type;

    private boolean following;
    private int programCount;

    private int userCount;
    private double starCount;

    private String followId;

    public Professional(String id, String photoUrl, String userPhotoUrl, String name,
                        String desc, ProfessionalType type, boolean following,
                        int programCount, int userCount, double starCount) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.userPhotoUrl = userPhotoUrl;
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.following = following;
        this.programCount = programCount;
        this.userCount = userCount;
        this.starCount = starCount;
    }

    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
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

    public ProfessionalType getType() {
        return type;
    }

    public void setType(ProfessionalType type) {
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
