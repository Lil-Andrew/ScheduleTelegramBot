package org.example.api.managers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.api.model.Group;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupsManager {
    @JsonProperty("data")
    private List<Group> groups;

    public Group getGroupById(String id) {
        for (Group group : groups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    public List<Group> getGroupsByContainsText(String groupName) {
        List<Group> groupsList = new ArrayList<>();
        if (groups != null && groupName.length() >= 2) {
            for (Group group : groups) {
                if (group.getName().toLowerCase().contains(groupName.trim().toLowerCase())) {
                    groupsList.add(group);
                }
            }
            return groupsList;
        }
        return null;
    }
}
