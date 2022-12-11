package edu.northeastern.numad22fa_team27.workout.models.DAO;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.Group;

public class GroupDAO {
    public String groupName;
    public List<String> members;
    public String adminID;
    public String groupChatId;
    public boolean acceptingMembers;

    public GroupDAO() {}

    public GroupDAO(Group g) {
        this.groupName = g.getGroupName();
        this.members = new ArrayList<>(g.getMembers());
        this.adminID = g.getAdminID();
        this.acceptingMembers = g.getAcceptingMembers();
        this.groupChatId = g.getGroupChatId();
    }

    @Override
    public String toString() {
        return "GroupDAO{" +
                "groupName='" + groupName + '\'' +
                ", members=" + members +
                ", adminID='" + adminID + '\'' +
                ", groupChatId='" + groupChatId + '\'' +
                ", acceptingMembers=" + acceptingMembers +
                '}';
    }
}
