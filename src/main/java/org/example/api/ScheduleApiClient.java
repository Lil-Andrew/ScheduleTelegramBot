package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.api.managers.CurrentDataManager;
import org.example.api.managers.GroupsManager;
import org.example.api.managers.ScheduleManager;
import org.example.api.model.CurrentData;
import org.example.utils.EnvConfig;

import java.io.IOException;

public class ScheduleApiClient {

    private GroupsManager groupsManager;
    private CurrentDataManager currentData;

    public void getData() {
        groupsManager = makeRequest(EnvConfig.get("GET_GROUP_API"), GroupsManager.class);
        getCurrentData();
    }

    private <T> T makeRequest(String api, Class<T> clazz) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(api).build();
        try(Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonResponse, clazz);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ScheduleManager getScheduleManagerForGroup(String groupId) {
        return makeRequest(EnvConfig.get("GET_SCHEDULE_API", groupId), ScheduleManager.class);
    }

    public CurrentDataManager getCurrentData() {
        currentData = makeRequest(EnvConfig.get("CURRENT_DATA"), CurrentDataManager.class);
        return currentData;
    }

    public GroupsManager getGroupsManager() {
        return groupsManager;
    }
}
