package com.mac.ekchitthi.Stamp;

public class StampModel {

    private String stamp_id,stamp_distance,stamp_days,stamp_word,stamp_from,stamp_to,stamp_date,stamp_image;
    private long timeStamp;

    public StampModel() {
    }

    public StampModel(String stamp_id, String stamp_distance, String stamp_days, String stamp_word, String stamp_from, String stamp_to, String stamp_date, String stamp_image,long timeStamp) {
        this.stamp_id = stamp_id;
        this.stamp_distance = stamp_distance;
        this.stamp_days = stamp_days;
        this.stamp_word = stamp_word;
        this.stamp_from = stamp_from;
        this.stamp_to = stamp_to;
        this.stamp_date = stamp_date;
        this.stamp_image=stamp_image;
        this.timeStamp=timeStamp;
    }

    public String getStamp_id() {
        return stamp_id;
    }

    public void setStamp_id(String stamp_id) {
        this.stamp_id = stamp_id;
    }

    public String getStamp_distance() {
        return stamp_distance;
    }

    public void setStamp_distance(String stamp_distance) {
        this.stamp_distance = stamp_distance;
    }

    public String getStamp_days() {
        return stamp_days;
    }

    public void setStamp_days(String stamp_days) {
        this.stamp_days = stamp_days;
    }

    public String getStamp_word() {
        return stamp_word;
    }

    public void setStamp_word(String stamp_word) {
        this.stamp_word = stamp_word;
    }

    public String getStamp_from() {
        return stamp_from;
    }

    public void setStamp_from(String stamp_from) {
        this.stamp_from = stamp_from;
    }

    public String getStamp_to() {
        return stamp_to;
    }

    public void setStamp_to(String stamp_to) {
        this.stamp_to = stamp_to;
    }

    public String getStamp_date() {
        return stamp_date;
    }

    public void setStamp_date(String stamp_date) {
        this.stamp_date = stamp_date;
    }

    public String getStamp_image() {
        return stamp_image;
    }

    public void setStamp_image(String stamp_image) {
        this.stamp_image = stamp_image;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
