package edu.northeastern.numad22fa_team27.workout.models;

import static java.lang.System.out;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserDAO {
    private String username;
    private String encryptedPassword;

    // List of the usernames of our friends
    private List<String> friendUsernames;

    // List of the IDs for the groups we've joined
    private List<UUID> joinedGroups;

    // Maps workout type -> (# days in streak, last day in streak)
    private Map<WorkoutCategory, Pair<Integer, LocalDate>> currentCategoryStreaks;

    // Maps workout type -> # days in best streak
    private Map<WorkoutCategory, Integer> bestCategoryStreaks;

    /**
     * New user constructor
     * @param username Unique string identifying user
     * @param encryptedPassword Hashed password
     */
    public UserDAO(String username, String encryptedPassword) {
        this.friendUsernames = new ArrayList<>();
        this.joinedGroups = new ArrayList<>();
        this.currentCategoryStreaks = new HashMap<>();
        this.bestCategoryStreaks = new HashMap<>();
    }


    /**
     * Complete object constructor
     * @param username Unique string identifying user
     * @param encryptedPassword Hashed password
     * @param friendUsernames List of the unique usernames of friends
     * @param joinedGroups List of UUIDs for the groups this user has joined
     * @param currentCategoryStreaks Map of streak category to current streak info
     * @param bestCategoryStreaks Map of streak category to best streak info
     */
    public UserDAO(String username, String encryptedPassword, List<String> friendUsernames, List<UUID> joinedGroups, Map<WorkoutCategory, Pair<Integer, LocalDate>> currentCategoryStreaks, Map<WorkoutCategory, Integer> bestCategoryStreaks) {
        this.username = username;
        this.friendUsernames = friendUsernames;
        this.joinedGroups = joinedGroups;
        this.encryptedPassword = encryptedPassword;
        this.currentCategoryStreaks = currentCategoryStreaks;
        this.bestCategoryStreaks = bestCategoryStreaks;
    }

    /**
     * Record a workout this user has performed
     * @param workout Workout just now finished
     * @param when Timestamp when the workout occurred
     */
    public void recordWorkout(@NonNull WorkoutDAO workout, LocalDate when) {
        for (WorkoutCategory w : workout.getCategoriesPresent()) {
            this.addToStreak(w, when);
        }
    }

    /**
     * Record that a workout category was practiced today, and update any streaks
     * @param w Workout just now finished
     * @param when Timestamp when the workout occurred
     *
     * @implNote This method assumes workouts are logged chronologically
     */
    private void addToStreak(WorkoutCategory w, LocalDate when) {
        if (!currentCategoryStreaks.containsKey(w)) {
            // New streak, nothing was there before.
            currentCategoryStreaks.put(w, new Pair<>(1, when));
        } else {
            LocalDate lastDay = currentCategoryStreaks.get(w).second;
            long hoursPassed = Math.abs(Duration.between(when.atStartOfDay(), lastDay.atStartOfDay()).toHours());

            // We'll give a bit of leniency here and ask for 30 hours or less, instead of 24
            if (hoursPassed > 30) {
                // Streak was broken, more than a day between workouts
                currentCategoryStreaks.put(w, new Pair<>(1, when));
            } else if (hoursPassed >= 20) {
                // Continue the streak, it's been a day
                currentCategoryStreaks.put(w, new Pair<>(currentCategoryStreaks.get(w).first + 1, when));
            }
        }

        // Keep track of best streaks
        if (!bestCategoryStreaks.containsKey(w) || bestCategoryStreaks.get(w) < currentCategoryStreaks.get(w).first) {
            bestCategoryStreaks.put(w, currentCategoryStreaks.get(w).first);
        }
    }

    public List<String> getFriendUsernames() {
        return friendUsernames;
    }

    public void setFriendUsernames(List<String> friendUsernames) {
        this.friendUsernames = friendUsernames;
    }

    public List<UUID> getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(List<UUID> joinedGroups) {
        this.joinedGroups = joinedGroups;
    }

    /**
     * @param w Workout category
     * @return Number of days in current streak
     */
    public int getCurrentStreak(WorkoutCategory w) {
        if (!this.currentCategoryStreaks.containsKey(w)) {
            return 0;
        }
        return this.currentCategoryStreaks.get(w).first;
    }

    /**
     * @param w Workout category
     * @return Number of days in best streak
     */
    public int getBestStreak(WorkoutCategory w) {
        if (!this.bestCategoryStreaks.containsKey(w)) {
            return 0;
        }
        return this.bestCategoryStreaks.get(w);
    }
}
