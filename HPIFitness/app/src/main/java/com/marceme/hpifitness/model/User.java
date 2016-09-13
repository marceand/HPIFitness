package com.marceme.hpifitness.model;

import io.realm.RealmObject;

/**
 * Created by Marcel on 9/12/2016.
 */
public class User extends RealmObject {
    private String id;
    private String firstName;
    private String username;
    private String password;
    private long   totalTimeWalk;
    private float   distanceCovered;
    private float   pace;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public float getPace() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public void updateDistanceCovered(float distanceWalked) {
        this.distanceCovered = this.distanceCovered + distanceWalked;
    }

    public void updateTotalTimeWalk(long timeWalked) {
        this.totalTimeWalk = this.totalTimeWalk+timeWalked;
    }
}
