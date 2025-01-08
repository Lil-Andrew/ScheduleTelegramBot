package org.example.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleData {
    @JsonProperty("groupCode")
    private String groupCode;

    @JsonProperty("scheduleFirstWeek")
    private List<DaySchedule> scheduleFirstWeek;

    @JsonProperty("scheduleSecondWeek")
    private List<DaySchedule> scheduleSecondWeek;

    public List<DaySchedule> getScheduleFirstWeek() {
        return scheduleFirstWeek;
    }

    public List<DaySchedule> getScheduleSecondWeek() {
        return scheduleSecondWeek;
    }

    public String getGroupCode() {
        return groupCode;
    }
}
