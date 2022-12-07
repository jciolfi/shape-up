package edu.northeastern.numad22fa_team27.workout.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;

public class Group {
    private String groupID;
    private String groupName;
    private Set<String> members;
    private String adminID;
    private boolean acceptingMembers;

    public Group(String groupName, String creatorID) {
        this.groupID = String.valueOf(UUID.randomUUID());
        this.groupName = groupName;
        members = new HashSet<>(){{ add(creatorID); }};
        this.adminID = creatorID;
        this.acceptingMembers = true;
    }

    public Group(GroupDAO g, String groupID) {
        setFromGroupDAO(g, groupID);
    }

    public String getGroupID() {
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

    public boolean getAcceptingMembers() {
        return acceptingMembers;
    }

    public void setFromGroupDAO(GroupDAO g, String groupID) {
        this.groupID = groupID;
        this.groupName = Util.nullOrDefault(g.groupName, "");
        this.members = new HashSet<>(Util.nullOrDefault(g.members, new ArrayList<>()));
        this.adminID = Util.nullOrDefault(g.adminID, "");
        this.acceptingMembers = g.acceptingMembers;
    }
}
