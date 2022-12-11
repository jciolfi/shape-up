package edu.northeastern.numad22fa_team27.workout.services;

import android.widget.TextView;

import edu.northeastern.numad22fa_team27.workout.callbacks.WorkoutCallback;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public interface IFirestoreService {
    /**
     * Create a group with the given groupName and userID
     * @param groupName name for the new group
     * @param callback executed on the new group returned by the query
     * @return true if group could be created, false otherwise
     */
    boolean tryCreateGroup(String groupName, WorkoutCallback callback);

    boolean tryChangeGroupPrivacy(String groupID, boolean isPublic);

    /**
     * Find workout by workoutName and/or workoutCategory
     * @param workoutName the name of the workout, matches on case-sensitive prefix
     * @param workoutCategory the category for the workout
     * @param callback executed on the entries returned by the query
     */
    void findWorkoutsByCriteria(String workoutName, WorkoutCategory workoutCategory, double maxDifficulty, double minDifficulty, WorkoutCallback callback, int resultLimit, boolean reverseOrder);

    /**
     * Find users with a given username, matches on case-sensitive prefix
     * @param username entered username to query on
     * @param callback executed on the entries returned by the query
     */
    void findUsersByUsername(String username, WorkoutCallback callback, boolean reverseOrder);

    /**
     * Find groups the current user is a part of
     * @param callback executed on the entries returned by the query
     */
    void findUserGroups(WorkoutCallback callback, TextView noGroupsText);

    /**
     * Find groups with a given groupName
     * @param groupName entered group name to query on
     * @param callback executed on the entries returned by the query
     */
    void findGroupsByName(String groupName, WorkoutCallback callback, boolean reverseOrder);

    /**
     * Get the global leaderboard by category sorted by best streak, limited to 100
     * @param callback executed on the entries returned by the query
     */
    void findStreaksLeaderboard(WorkoutCategory category, WorkoutCallback callback);

    /**
     * Get a group with the given ID
     * @param groupID ID for the group
     * @param callback executed on the entry returned by the query
     */
    void getGroupByID(String groupID, WorkoutCallback callback);

    /**
     * Try to join the group with the corresponding groupID. Users limited to 10 groups
     * @param groupID the ID for the group
     */
    boolean tryJoinGroup(String groupID);

    /**
     * Try to join the group with the corresponding groupID
     * @param groupID the ID for the group
     */
    void leaveGroup(String groupID);

    /**
     * Retrieve details for user with given ID
     * @param userID the user's ID
     * @param callback executed on the entry returned by the query
     */
    void getUserByID(String userID, WorkoutCallback callback);

    /**
     * Try to send a friend request for the given friend
     * @param friendID ID of the user to friend
     * @return true if this userID shows up in friend's incoming requests, false otherwise
     */
    boolean tryRequestFriend(String friendID);

    /**
     * Try to accept a friend request
     * @param friendID ID of user that friended this user
     * @return true if users can be friends, false otherwise
     */
    boolean tryAcceptFriendRequest(String friendID);

    /**
     * Try to reject a friend request
     * @param friendID ID of user that friended this user
     * @return true if friend request no longer present, false otherwise
     */
    boolean tryRejectFriendRequest(String friendID);

    /**
     * Try to remove friend - removes each other from both users' friends list
     * @param friendID the ID of friend to remove
     */
    void removeFriend(String friendID);

    void johntest();
}
