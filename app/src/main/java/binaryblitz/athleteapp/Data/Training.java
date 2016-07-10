package binaryblitz.athleteapp.Data;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;

public class Training {
    private String id;

    private String programId;
    private String name;
    private String programName;
    private int exCount;
    private Calendar date;
    private int time;

    private String desc;

    private ArrayList<Pair<String, String>> tips;

    public Training(String id, String name, String programName, int exCount,
                    Calendar date, int time,
                    String desc, String programId, ArrayList<Pair<String, String>> tips) {
        this.desc = desc;
        this.name = name;
        this.programName = programName;
        this.exCount = exCount;
        this.date = date;
        this.time = time;
        this.id = id;
        this.programId = programId;
        this.tips = tips;
    }

    public ArrayList<Pair<String, String>> getTips() {
        return tips;
    }

    public void setTips(ArrayList<Pair<String, String>> tips) {
        this.tips = tips;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
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
