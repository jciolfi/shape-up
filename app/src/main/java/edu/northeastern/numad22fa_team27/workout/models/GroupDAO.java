package edu.northeastern.numad22fa_team27.workout.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.Util;

public class GroupDAO {
    private UUID groupID;
    private String name;
    private Set<String> members;

    public GroupDAO(String groupName, String creatorUsername) {
        groupID = UUID.randomUUID();
        this.name = groupName;
        // new LinkedList<String>() {{  add(genres.getSelectedItem().toString()); }},
        this.members = new HashSet<>() {{ add(creatorUsername); }};
    }

    // ---------- UUID Code ----------
    /**
     * Get ID for this group, returned as a string
     * @return this group's ID
     */
    public UUID getGroupID() {
        return this.groupID;
    }

    // ---------- Group Name Code ----------
    /**
     * Get the name for this group
     * @return name of this group
     */
    public String getName() {
        return name;
    }

    /**
     * Change the name of this group
     * @param newName new name for this group
     * @return true if name was successfully changed, false otherwise
     */
    public boolean tryChangeName(String newName) {
        if (Util.stringIsNullOrEmpty(newName)) {
            return false;
        }

        this.name = newName;
        return true;
    }

    // ---------- Group Members Code ----------
    /**
     * Get members in this group, returned as a list
     * @return members' usernames in this group
     */
    public Set<String> getMembers() {
        return new HashSet<>(this.members);
    }

    /**
     * Add member to this group
     * @param username member joining this group
     * @return true if they were newly added to group, false otherwise
     */
    public boolean tryAddMember(String username) {
        if (this.members.contains(username)) {
            return false;
        }

        return this.members.add(username);
    }

    /**
     * Remove member from this group
     * @param username member leaving this group
     * @return true if they're in this group and were removed, false otherwise.
     */
    public boolean tryRemoveMember(String username) {
        if (!this.members.contains(username)) {
            return false;
        }

        return this.members.remove(username);
    }

    // --------------------------------------------------------------------
    @Override
    public String toString() {
        return "GroupDAO{" +
                "groupID=" + groupID +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }

    public class Group {
        // Fields must be public to be set in Firebase DB
        public String name;
        public List<String> members;

        private Group(String name, List<String> members) {
            this.name = name;
            this.members = members;
        }
    }

    public Group pack() {
        return new Group(this.name, new ArrayList<>(this.members));
    }
}
