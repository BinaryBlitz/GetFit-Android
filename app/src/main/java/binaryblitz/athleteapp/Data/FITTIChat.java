package binaryblitz.athleteapp.Data;

public class FITTIChat {
    private int avatar;
    private String name;
    private String time;
    private String last;
    private int unRead;

    public FITTIChat(int unRead, String name, String time, String last, int avatar) {
        this.unRead = unRead;
        this.name = name;
        this.time = time;
        this.last = last;
        this.avatar = avatar;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public int getUnRead() {
        return unRead;
    }

    public void setUnRead(int unRead) {
        this.unRead = unRead;
    }
}
