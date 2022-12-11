package edu.northeastern.numad22fa_team27.workout.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;

public class Group implements Summarizeable {
    private String groupID;
    private String groupName;
    private Set<String> members;
    private String adminID;
    public String groupChatId;
    private boolean acceptingMembers;

    public Group(String groupName, String creatorID) {
        this.groupID = String.valueOf(UUID.randomUUID());
        this.groupName = groupName;
        this.members = new HashSet<>(){{ add(creatorID); }};
        this.adminID = creatorID;
        this.acceptingMembers = true;
        this.groupChatId = "";
    }

    public Group(GroupDAO g, String groupID) {
        setFromGroupDAO(g, groupID);
    }

    public void setFromGroupDAO(GroupDAO g, String groupID) {
        this.groupID = groupID;
        this.groupName = Util.nullOrDefault(g.groupName, "");
        this.members = new HashSet<>(Util.nullOrDefault(g.members, new ArrayList<>()));
        this.adminID = Util.nullOrDefault(g.adminID, "");
        this.acceptingMembers = g.acceptingMembers;
        this.groupChatId = g.groupChatId;
    }

    @Override
    public String getTitle() {
        return getGroupName();
    }

    @Override
    public String getMisc() {
        return "";
    }

    @Override
    public String getImage() {
        return null;
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

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Group{" +
                "groupID='" + groupID + '\'' +
                ", groupName='" + groupName + '\'' +
                ", members=" + members +
                ", adminID='" + adminID + '\'' +
                ", groupChatId='" + groupChatId + '\'' +
                ", acceptingMembers=" + acceptingMembers +
                '}';
    }
}
