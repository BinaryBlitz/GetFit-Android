package binaryblitz.athleteapp.Data;

import java.util.Calendar;

public class Chat {
    private int avatar;
    private String name;
    private Calendar time;
    private String last;
    private int unRead;

    private String subscriptionId;

    public Chat(int unRead, String name, Calendar time, String last, int avatar, String subscriptionId) {
        this.unRead = unRead;
        this.name = name;
        this.time = time;
        this.last = last;
        this.avatar = avatar;
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
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

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
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
