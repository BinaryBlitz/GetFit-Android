package binaryblitz.athleteapp.Data;

import java.util.ArrayList;
import java.util.Date;

public class FITTITraining {
    private String id;

    private int intId;

    private String name;
    private String type;
    private int exCount;
    private Date date;
    private int time;

    private ArrayList<FITTITrainingPart> parts;

    private FITTIProfessional owner;

    private String desc;

    public FITTITraining(String id, int intId, String name, String type, int exCount,
                         Date date, int time, ArrayList<FITTITrainingPart> parts, FITTIProfessional owner,
                         String desc) {
        this.desc = desc;
        this.intId = intId;
        this.name = name;
        this.type = type;
        this.exCount = exCount;
        this.date = date;
        this.time = time;
        this.parts = parts;
        this.owner = owner;
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FITTIProfessional getOwner() {
        return owner;
    }

    public void setOwner(FITTIProfessional owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIntId() {
        return intId;
    }

    public void setIntId(int intId) {
        this.intId = intId;
    }

    public ArrayList<FITTITrainingPart> getParts() {
        return parts;
    }

    public void setParts(ArrayList<FITTITrainingPart> parts) {
        this.parts = parts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getExCount() {
        return exCount;
    }

    public void setExCount(int exCount) {
        this.exCount = exCount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
