package binaryblitz.athleteapp.Data;

import java.util.Calendar;

public class Comment {
    private String id;

    private String text;

    private String userName;
    private String userId;

    private Calendar date;

    private String avatarUrl;

    public Comment(String id, String text, String userName, String userId, Calendar date, String avatarUrl) {
        this.id = id;
        this.text = text;
        this.userName = userName;
        this.userId = userId;
        this.date = date;
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
