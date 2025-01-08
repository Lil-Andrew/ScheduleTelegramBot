package org.example.api.managers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Main;
import org.example.api.model.DaySchedule;
import org.example.api.model.ScheduleData;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleManager {
    @JsonProperty("data")
    private ScheduleData data;

    public ScheduleData getData() {
        return data;
    }

    public List<DaySchedule> getDay(int offset) {
        CurrentDataManager currentData = Main.scheduleApiClient.getCurrentData();
        int targetDay = currentData.getCurrentDay() + offset;
        if (targetDay < 1 || targetDay > 5) {
            return List.of();
        }
        List<DaySchedule> currentWeek = currentData.getCurrentWeek() == 0 ? data.getScheduleFirstWeek() : data.getScheduleSecondWeek();
        DaySchedule daySchedule = currentWeek.get(currentData.getCurrentDay() - (offset == 0 ? 1 : 0));
        return List.of(daySchedule);
    }

}
