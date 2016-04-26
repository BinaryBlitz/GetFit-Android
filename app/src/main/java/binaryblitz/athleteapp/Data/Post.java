package binaryblitz.athleteapp.Data;

import java.util.ArrayList;
import java.util.Calendar;

public class Post {
    private String id;

    private String userName;
    private String userId;
    private String userPhotoUrl;

    @SuppressWarnings("unused")
    private int userPhotoResId;

    private String desc;

    private String photoUrl;

    @SuppressWarnings("unused")
    private int photoResId;

    private Calendar date;

    private int likeCount;
    private boolean liked;

    private String likeId;

    private ArrayList<Comment> comments;

    public Post(String id, String userName, String userId, String userPhotoUrl,
                int userPhotoResId, String desc, String photoUrl,
                int photoResId, Calendar date, int likeCount, ArrayList<Comment> comments, String likeId, boolean liked) {
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.userPhotoUrl = userPhotoUrl;
        this.userPhotoResId = userPhotoResId;
        this.desc = desc;
        this.photoUrl = photoUrl;
        this.photoResId = photoResId;
        this.date = date;
        this.likeCount = likeCount;
        this.comments = comments;
        this.likeId = likeId;
        this.liked = liked;
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

    public int getUserPhotoResId() {
        return userPhotoResId;
    }

    public void setUserPhotoResId(int userPhotoResId) {
        this.userPhotoResId = userPhotoResId;
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

    public int getPhotoResId() {
        return photoResId;
    }

    public void setPhotoResId(int photoResId) {
        this.photoResId = photoResId;
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

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
