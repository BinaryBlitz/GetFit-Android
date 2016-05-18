package binaryblitz.athleteapp.Data;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;

public class SubscriptionsSet {
    public static class Subscription {
        private String id;
        private String trainerId;

        public Subscription(String id, String trainerId) {
            this.id = id;
            this.trainerId = trainerId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTrainerId() {
            return trainerId;
        }

        public void setTrainerId(String trainerId) {
            this.trainerId = trainerId;
        }
    }

    private static ArrayList<Subscription> list = new ArrayList<>();
    private static SubscriptionsSet set;

    public static SubscriptionsSet load() {
        if (set == null) {
            synchronized (SubscriptionsSet.class) {
                if (set == null) {
                    set = new SubscriptionsSet();
                }
            }
        }
        return set;
    }

    private SubscriptionsSet() {
        if (list == null) {
            list = new ArrayList<>();
        }
    }

    public void clear() {
        list.clear();
    }

    public void add(Subscription subscription) {
        list.add(subscription);
    }

    public Subscription get(String trainerId) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getTrainerId().equals(trainerId)) {
                return list.get(i);
            }
        }

        return null;
    }
}
