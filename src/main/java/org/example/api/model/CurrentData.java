package org.example.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentData {
    @JsonProperty("currentWeek")
    public int currentWeek;

    @JsonProperty("currentDay")
    public int currentDay;

    @JsonProperty("currentLesson")
    public int currentLesson;
}
