package binaryblitz.athleteapp.Data;

public class FITTITrainingPart {
    private String id;

    private String name;
    private int weight;
    private int count;
    private int reps;
    private int time;

    private String desc;

    public FITTITrainingPart(String id, String name, int weight, int count, int reps, int time, String desc) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.count = count;
        this.reps = reps;
        this.time = time;
        this.desc = desc;
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
