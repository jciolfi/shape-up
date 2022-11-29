package edu.northeastern.numad22fa_team27.workout.models.DAO;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.Group;

public class GroupDAO {
    // name of group
    public String groupName;

    // UUIDs as strings
    public List<String> members;

    public GroupDAO() {}

    public GroupDAO(Group g) {
        this.groupName = g.getGroupName();
        this.members = new ArrayList<>(g.getMembers());
    }
}
