package edu.northeastern.numad22fa_team27.workout.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.Util;

public class GroupDAO {
    private UUID groupID;
    private String name;
    private Set<UUID> members;

    public GroupDAO(String groupName, UUID creatorID) {
        groupID = UUID.randomUUID();
        this.name = groupName;
        // new LinkedList<String>() {{  add(genres.getSelectedItem().toString()); }},
        this.members = new HashSet<>() {{ add(creatorID); }};
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
    public Set<UUID> getMembers() {
        return new HashSet<>(this.members);
    }

    /**
     * Add member to this group
     * @param userID member joining this group
     * @return true if they were newly added to group, false otherwise
     */
    public boolean tryAddMember(UUID userID) {
        if (this.members.contains(userID)) {
            return false;
        }

        return this.members.add(userID);
    }

    /**
     * Remove member from this group
     * @param userID member leaving this group
     * @return true if they're in this group and were removed, false otherwise.
     */
    public boolean tryRemoveMember(UUID userID) {
        if (!this.members.contains(userID)) {
            return false;
        }

        return this.members.remove(userID);
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

    // This is inserted into the DB (without a reference to groupID & list of string IDs for members)
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
        List<String> membersString = this.members.stream().map(String::valueOf).collect(Collectors.toList());
        return new Group(this.name, membersString);
    }
}
