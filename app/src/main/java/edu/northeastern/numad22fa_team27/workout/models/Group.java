package edu.northeastern.numad22fa_team27.workout.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;

public class Group {
    private final UUID groupID;
    private final String groupName;
    private final Set<String> members;
    private final String adminID;
    private final Boolean acceptingMembers;

    public Group(String groupName, String creatorID) {
        this.groupID = UUID.randomUUID();
        this.groupName = groupName;
        members = new HashSet<>(){{ add(creatorID); }};
        this.adminID = creatorID;
        this.acceptingMembers = true;
    }

    public Group(GroupDAO g, String groupID) {
        this.groupID = UUID.fromString(groupID);
        this.groupName = g.groupName;
        this.members = new HashSet<>(g.members);
        this.adminID = g.adminID;
        this.acceptingMembers = g.acceptingMembers;
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

    public String getAdminID() {
        return adminID;
    }

    public Boolean getAcceptingMembers() {
        return acceptingMembers;
    }
}
