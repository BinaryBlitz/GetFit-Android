package binaryblitz.athleteapp.Data;

import java.util.ArrayList;

public class MyProgramsSet {
    private static ArrayList<String> list = new ArrayList<>();
    private static MyProgramsSet set;

    public static MyProgramsSet load() {
        if (set == null) {
            synchronized (SubscriptionsSet.class) {
                if (set == null) {
                    set = new MyProgramsSet();
                }
            }
        }
        return set;
    }

    private MyProgramsSet() {
        if (list == null) {
            list = new ArrayList<>();
        }
    }

    public void clear() {
        list.clear();
    }

    public void add(String subscription) {
        list.add(subscription);
    }

    public boolean find(String id) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(id)) {
                return true;
            }
        }

        return false;
    }
}
