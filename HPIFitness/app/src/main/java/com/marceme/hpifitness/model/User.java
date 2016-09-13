package com.marceme.hpifitness.model;

import io.realm.RealmObject;

/**
 * Created by Marcel on 9/12/2016.
 */
public class User extends RealmObject {
    private String firstName;
    private String username;
    private String password;
    private long   totalTimeWalk;
    private float   distanceCovered;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTotalTimeWalk() {
        return totalTimeWalk;
    }

    public void setTotalTimeWalk(long totalTimeWalk) {
        this.totalTimeWalk = totalTimeWalk;
    }

    public float getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(float distanceCovered) {
        this.distanceCovered = distanceCovered;
    }
}
