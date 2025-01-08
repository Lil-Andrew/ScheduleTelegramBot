package org.example.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DaySchedule {
    @JsonProperty("day")
    private String day;

    @JsonProperty("pairs")
    private List<Pair> pairs;

    public String getDay() {
        return day;
    }

    public List<Pair> getPairs() {
        return pairs;
    }
}
