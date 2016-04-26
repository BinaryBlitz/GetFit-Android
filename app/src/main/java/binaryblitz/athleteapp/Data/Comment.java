package binaryblitz.athleteapp.Data;

public class Comment {
    private String id;

    private String text;

    private String userName;
    private String userId;

    public Comment(String id, String text, String userName, String userId) {
        this.id = id;
        this.text = text;
        this.userName = userName;
        this.userId = userId;
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
