package binaryblitz.athleteapp.Data;

import java.util.Calendar;

public class Message {
    private String id;
    private String text;
    private Calendar time;

    private String photoUrl;
    private String photoThumb;

    private boolean myMessage;

    public Message(String id, String text, Calendar time, String photoUrl, boolean myMessage, String photoThumb) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.photoUrl = photoUrl;
        this.myMessage = myMessage;
        this.photoThumb = photoThumb;
    }

    public String getPhotoThumb() {
        return photoThumb;
    }

    public void setPhotoThumb(String photoThumb) {
        this.photoThumb = photoThumb;
    }

    public String getDate() {
        String date = "";

        if(time.get(Calendar.DAY_OF_MONTH) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            date += Integer.toString(time.get(Calendar.DAY_OF_MONTH)) + "." +
                    (time.get(Calendar.MONTH) + 1) + "." +
                    Integer.toString(time.get(Calendar.YEAR));
        }
        date += " ";
        date += (time.get(Calendar.HOUR_OF_DAY) > 9 ?
                Integer.toString(time.get(Calendar.HOUR_OF_DAY)) :
                "0" + Integer.toString(time.get(Calendar.HOUR_OF_DAY)))
                + ":" + (time.get(Calendar.MINUTE) > 9 ?
                Integer.toString(time.get(Calendar.MINUTE)) :
                "0" + Integer.toString(time.get(Calendar.MINUTE)));

        return date;
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

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isMyMessage() {
        return myMessage;
    }

    public void setMyMessage(boolean myMessage) {
        this.myMessage = myMessage;
    }
}
