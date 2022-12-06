package edu.northeastern.numad22fa_team27.workout.services;

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
    void findWorkoutsByCriteria(String workoutName, WorkoutCategory workoutCategory, double maxDifficulty, double minDifficulty, WorkoutCallback callback, int resultLimit);

    /**
     * Find users with a given username, matches on case-sensitive prefix
     * @param username entered username to query on
     * @param callback executed on the entries returned by the query
     */
    void findUsersByUsername(String username, WorkoutCallback callback);

    /**
     * Find groups the current user is a part of
     * @param callback executed on the entries returned by the query
     */
    void findUserGroups(WorkoutCallback callback);

    /**
     * Find groups with a given groupName
     * @param groupName entered group name to query on
     * @param callback executed on the entries returned by the query
     */
    void findGroupsByName(String groupName, WorkoutCallback callback);

    /**
     * Get the global leaderboard by category sorted by best streak, limited to 100
     * @param callback executed on the entries returned by the query
     */
    void findStreaksLeaderboard(WorkoutCategory category, WorkoutCallback callback);
}
