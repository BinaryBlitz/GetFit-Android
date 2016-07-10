package binaryblitz.athleteapp.Data;

public class TrainingPart {
    private String id;

    private String name;
    private int weight;
    private int count;
    private int reps;
    private int time;

    private String desc;

    private boolean completed;
    private int number;

    private String videoUrl;

    public TrainingPart(String id, String name, int weight, int count, int reps, int time, String desc,
                        boolean completed, int number, String videoUrl) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.count = count;
        this.reps = reps;
        this.time = time;
        this.desc = desc;
        this.completed = completed;
        this.number = number;
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
