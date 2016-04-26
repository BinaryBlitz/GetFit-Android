package binaryblitz.athleteapp.Data;

import java.util.Calendar;

public class User {
    private String id;
    private String name;

    private String phoneNumber;

    private String description;
    private String avatarUrl;
    private String bannerUrl;

    private int weight;
    private int height;

    private boolean gender;
    private Calendar birthDate;

    public User(String id, String name, String phoneNumber, String description, String avatarUrl,
                String bannerUrl, int weight, int height, boolean gender, Calendar birthDate) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.bannerUrl = bannerUrl;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public User(String id, String name, String avatarUrl, String bannerUrl) {
        this.name = name;
        this.id = id;
        if(avatarUrl  != null) {
            this.avatarUrl = avatarUrl.equals("null") ? null : avatarUrl;
        } else {
            this.avatarUrl = avatarUrl;
        }

        if(bannerUrl != null) {
            this.bannerUrl = bannerUrl.equals("null") ? null : bannerUrl;
        } else {
            this.bannerUrl = bannerUrl;
        }
    }

    public String asString() {
        return id + "entity" + name + "entity" + avatarUrl + "entity" + bannerUrl;
    }

    public static User fromString(String str) {
        String[] strings = str.split("entity");
        return new User(strings[0], strings[1], strings[2], strings[3]);
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Calendar getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Calendar birthDate) {
        this.birthDate = birthDate;
    }
}
