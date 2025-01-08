package org.example.api.managers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.api.model.CurrentData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentDataManager {
    @JsonProperty("data")
    private CurrentData data;

    public CurrentData getData() {
        return data;
    }

    public int getCurrentWeek() {
        return data.currentWeek;
    }

    public int getCurrentDay() {
        return data.currentDay;
    }

    public int getCurrentLesson() {
        return data.currentLesson;
    }
}
