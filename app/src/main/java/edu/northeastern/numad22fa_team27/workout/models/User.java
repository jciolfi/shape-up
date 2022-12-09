package edu.northeastern.numad22fa_team27.workout.models;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.utilities.StoreablePair;

// TODO: a lot of these "lists" should really be sets
public class User implements Summarizeable {
    private final static String TAG = "User";
    private String userID;
    private String username;
    private String profilePic;

    // List of the IDs of this user's friends
    private List<String> friends;

    // List of IDs of the users who requested to friend this user
    private Set<String> incomingFriendRequests;

    // List of the IDs for the groups we've joined
    private List<String> joinedGroups;

    // Maps workout type -> (# days in streak, last day in streak)
    private Map<WorkoutCategory, Pair<Integer, LocalDate>> currentCategoryStreaks;

    // Maps workout type -> # days in best streak
    private Map<WorkoutCategory, Integer> bestCategoryStreaks;

    private Map<String, Integer> workoutCompletions;

    private List<String> chats;

    public User() { }

    /**
     * New user constructor
     * @param username Unique string identifying user
     * @param profilePic Link to the user's profile picture
     */

    public User(String username, String profilePic) {
        this.username = username;
        this.profilePic = profilePic;
        this.friends = new ArrayList<>();
        this.joinedGroups = new ArrayList<>();
        this.incomingFriendRequests = new HashSet<>();
        this.currentCategoryStreaks = new HashMap<>();
        this.bestCategoryStreaks = new HashMap<>();
        this.workoutCompletions = new HashMap<>();
        this.chats = new ArrayList<>();
    }

    /**
     * Complete object constructor
     * @param username Unique string identifying user
     * @param friends List of the unique usernames of friends
     * @param joinedGroups List of UUIDs for the groups this user has joined
     * @param currentCategoryStreaks Map of streak category to current streak info
     * @param bestCategoryStreaks Map of streak category to best streak info
     * @param workoutCompletions Map of workouts onto number of completions
     */
    public User(String username, String profilePic, List<String> friends, Set<String> incomingFriendRequests, List<String> joinedGroups, Map<WorkoutCategory, Pair<Integer, LocalDate>> currentCategoryStreaks, Map<WorkoutCategory, Integer> bestCategoryStreaks, Map<String, Integer> workoutCompletions, List<String> chats) {
        this.username = username;
        this.profilePic = profilePic;
        this.friends = friends;
        this.incomingFriendRequests = incomingFriendRequests;
        this.joinedGroups = joinedGroups;
        this.currentCategoryStreaks = currentCategoryStreaks;
        this.bestCategoryStreaks = bestCategoryStreaks;
        this.workoutCompletions = workoutCompletions;
        this.chats = chats;
    }

    public User(UserDAO userDAO, String userID) {
        setUserFromDAO(userDAO, userID);
    }

    /**
     * Record a workout this user has performed
     * @param workout Workout just now finished
     * @param when Timestamp when the workout occurred
     */
    public void recordWorkout(@NonNull Workout workout, LocalDate when) {
        // Keep track of the fact we finished this workout in particular
        workoutCompletions.put(
                workout.getWorkoutID(),
                workoutCompletions.getOrDefault(workout.getWorkoutID(), 0) + 1
        );

        // Compute streak logic
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

    public String getUsername() {
        return username;
    }

    public List<String> getFriends() {
        return friends;
    }

    public Set<String> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userID, user.userID) && Objects.equals(username, user.username) && Objects.equals(profilePic, user.profilePic) && Objects.equals(friends, user.friends) && Objects.equals(incomingFriendRequests, user.incomingFriendRequests) && Objects.equals(joinedGroups, user.joinedGroups) && Objects.equals(currentCategoryStreaks, user.currentCategoryStreaks) && Objects.equals(bestCategoryStreaks, user.bestCategoryStreaks) && Objects.equals(workoutCompletions, user.workoutCompletions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, username, profilePic, friends, incomingFriendRequests, joinedGroups, currentCategoryStreaks, bestCategoryStreaks, workoutCompletions);
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(List<String> joinedGroups) {
        this.joinedGroups = joinedGroups;
    }

    public Map<WorkoutCategory, Pair<Integer, LocalDate>> getCurrentCategoryStreaks() {
        return currentCategoryStreaks;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public Map<WorkoutCategory, Integer> getBestCategoryStreaks() {
        return bestCategoryStreaks;
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

    public String getUserID() {
        return userID;
    }

    public void setUserFromDAO(UserDAO userDAO, String userID) {
        this.userID = userID;

        this.username = userDAO.username == null ? "" : userDAO.username;
        this.friends = userDAO.friends == null ? new ArrayList<>() : userDAO.friends;

        this.joinedGroups = Util.nullOrDefault(userDAO.joinedGroups, new ArrayList<>());

        this.currentCategoryStreaks = new HashMap<>();
        if (userDAO.currentCategoryStreaks != null) {
            for (String category : userDAO.currentCategoryStreaks.keySet()) {
                StoreablePair<Integer, Long> info = userDAO.currentCategoryStreaks.get(category);
                this.currentCategoryStreaks.put(
                        WorkoutCategory.toCategory(category),
                        new Pair<>(info.getFirst(), Instant.ofEpochSecond(info.getSecond()).atZone(ZoneId.systemDefault()).toLocalDate())
                );
            }
        }

        this.bestCategoryStreaks = new HashMap<>();
        if (userDAO.bestCategoryStreaks != null) {
            for (String category : userDAO.bestCategoryStreaks.keySet()) {
                this.bestCategoryStreaks.put(WorkoutCategory.toCategory(category), userDAO.bestCategoryStreaks.get(category));
            }
        }

        this.incomingFriendRequests = new HashSet<>(Util.nullOrDefault(userDAO.incomingFriendRequests, new ArrayList<>()));

        this.profilePic = Util.nullOrDefault(userDAO.profilePic, "");

        this.workoutCompletions = Util.nullOrDefault(userDAO.workoutCompletions, new HashMap<>());

        this.chats = userDAO.chats;
    }

    public Map<String, Integer> getWorkoutCompletions() {
        return workoutCompletions;
    }

    public void setWorkoutCompletions(Map<String, Integer> workoutCompletions) {
        this.workoutCompletions = workoutCompletions;
    }

    public List<String> getChats() {
        return chats;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", friends=" + friends +
                ", incomingFriendRequests=" + incomingFriendRequests +
                ", joinedGroups=" + joinedGroups +
                ", currentCategoryStreaks=" + currentCategoryStreaks +
                ", bestCategoryStreaks=" + bestCategoryStreaks +
                ", workoutCompletions=" + workoutCompletions +
                '}';
    }

    @Override
    public String getTitle() {
        return this.getUsername();
    }

    @Override
    public String getMisc() {
        return "";
    }

    @Override
    public String getImage() {
        return this.getProfilePic();
    }
}
