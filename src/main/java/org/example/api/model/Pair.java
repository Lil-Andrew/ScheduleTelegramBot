package org.example.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pair {
    @JsonProperty("teacherName")
    private String teacherName;

    @JsonProperty("lecturerId")
    private String lecturerId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("time")
    private String time;

    @JsonProperty("name")
    private String name;

    @JsonProperty("place")
    private String place;

    @JsonProperty("tag")
    private String tag;

    public String getTeacherName() {
        return teacherName;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getTag() {
        return tag;
    }
}
