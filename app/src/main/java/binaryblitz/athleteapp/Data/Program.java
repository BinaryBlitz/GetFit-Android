package binaryblitz.athleteapp.Data;

public class Program {
    private String id;

    private String name;

    private String price;
    private String type;
    private int count;

    private String photoUrl;
    private String userPhotoUrl;

    private String desc;
    private String time;

    private int userCount;
    private double starCount;

    private String trainerName;
    private String trainerId;

    private String ratingId;

    public Program(String id, String name, String price,
                   String type, int count, String desc, String time, int userCount,
                   double starCount, String trainerName, String trainerId, String photoUrl, String ratingId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.count = count;
        this.desc = desc;
        this.time = time;
        this.userCount = userCount;
        this.starCount = starCount;
        this.trainerName = trainerName;
        this.trainerId = trainerId;
        this.photoUrl = photoUrl;
        this.ratingId = ratingId;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }
}
