package binaryblitz.athleteapp.Data;

import java.util.ArrayList;
import java.util.Calendar;

public class Post {
    private String id;

    private String userName;
    private String userId;
    private String userPhotoUrl;

    private String desc;

    private String photoUrl;

    private Calendar date;

    private int likeCount;
    private int commentsCount;
    private boolean liked;

    private String likeId;

    private String programName;
    private String programPrice;
    private String programType;
    private String programWorkouts;
    private String programId;

    public Post(String id, String userName, String userId, String userPhotoUrl,
                 String desc, String photoUrl,
                 Calendar date, int likeCount, int commentsCount, String likeId, boolean liked) {
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.userPhotoUrl = userPhotoUrl;
        this.desc = desc;
        this.photoUrl = photoUrl;
        this.date = date;
        this.likeCount = likeCount;
        this.commentsCount = commentsCount;
        this.likeId = likeId;
        this.liked = liked;

        this.programName = null;
        this.programPrice = null;
        this.programType = null;
        this.programWorkouts = null;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramPrice() {
        return programPrice;
    }

    public void setProgramPrice(String programPrice) {
        this.programPrice = programPrice;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getProgramWorkouts() {
        return programWorkouts;
    }

    public void setProgramWorkouts(String programWorkouts) {
        this.programWorkouts = programWorkouts;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
