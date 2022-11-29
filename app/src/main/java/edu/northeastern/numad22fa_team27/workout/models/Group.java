package edu.northeastern.numad22fa_team27.workout.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Group {
    private UUID groupID;
    private String groupName;
    private Set<String> members;

    public Group(String groupName, String creatorID) {
        this.groupID = UUID.randomUUID();
        this.groupName = groupName;
        members = new HashSet<>(){{ add(creatorID); }};
    }

    public UUID getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public Set<String> getMembers() {
        return members;
    }
}
