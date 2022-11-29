package edu.northeastern.numad22fa_team27.workout.services;

import java.util.UUID;

import edu.northeastern.numad22fa_team27.workout.callbacks.WorkoutCallback;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public interface IFirestoreService {
    /**
     * Create a group with the given groupName and userID
     * @param groupName name for the new group
     */
    void createGroup(String groupName);

    /**
     * Find workout by workoutName and/or workoutCategory
     * @param workoutName the name of the workout, matches on case-sensitive prefix
     * @param workoutCategory the category for the workout
     * @param callback executed on the entries returned by the query
     */
    void findWorkoutsByCriteria(String workoutName, WorkoutCategory workoutCategory, WorkoutCallback callback);

    /**
     * Find users with a given username, matches on case-sensitive prefix
     * @param username entered username to query on
     * @param callback executed on the entries returned by the query
     */
    void findUserByUsername(String username, WorkoutCallback callback);

    /**
     * Find groups a user is a part of
     * @param userID the ID for the user
     * @param callback executed on the entries returned by the query
     */
    void findUserGroups(String userID, WorkoutCallback callback);

    /**
     * Find groups with a given groupName
     * @param groupName entered group name to query on
     * @param callback executed on the entries returned by the query
     */
    void findGroupsByName(String groupName, WorkoutCallback callback);

    /**
     * Get the leaderboard for the friends of the given user
     * @param userID the user who's leaderboard will be displayed
     * @param callback executed on the entries returned by the query
     */
    void findStreaksLeaderboard(String userID, WorkoutCallback callback);
}
