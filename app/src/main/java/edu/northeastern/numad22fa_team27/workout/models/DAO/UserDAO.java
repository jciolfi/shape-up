package edu.northeastern.numad22fa_team27.workout.models.DAO;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.utilities.StoreablePair;

public class UserDAO {
    public String username;
    public List<String> friends;
    public List<String> incomingFriendRequests;
    public List<String> joinedGroups;
    public Map<String, StoreablePair<Integer, Long>> currentCategoryStreaks;
    public Map<String, Integer> bestCategoryStreaks;
    public Map<String, Integer> workoutCompletions;
    public String profilePic;
    public List<String> chats;

    public UserDAO() {}

    public UserDAO(User u) {
        this.username = Util.nullOrDefault(u.getUsername(), "");
        this.friends = Util.nullOrDefault(u.getFriends(), new ArrayList<>());
        this.incomingFriendRequests = new ArrayList<>(
                Util.nullOrDefault(u.getIncomingFriendRequests(), new HashSet<>()));
        this.joinedGroups = Util.nullOrDefault(u.getJoinedGroups(), new ArrayList<>()).stream()
                .map(String::valueOf).collect(Collectors.toList());

        // Load streak category information
        this.currentCategoryStreaks = u.getCurrentCategoryStreaks().entrySet().stream()
                .collect(Collectors.toMap((entry) -> entry.getKey().name(), (entry) -> new StoreablePair<>(entry.getValue().first, entry.getValue().second.atStartOfDay(ZoneId.systemDefault()).toEpochSecond())));
        this.bestCategoryStreaks = u.getBestCategoryStreaks().entrySet().stream()
                .collect(Collectors.toMap((entry) -> entry.getKey().name(), (entry) -> entry.getValue()));
        this.workoutCompletions = u.getWorkoutCompletions();
        this.profilePic = Util.nullOrDefault(u.getProfilePic(), "");
        this.chats = u.getChats();
    }

    @NonNull
    @Override
    public String toString() {
        return "UserDAO{" +
                "username='" + username + '\'' +
                ", friends=" + friends +
                ", incomingFriendRequests=" + incomingFriendRequests +
                ", joinedGroups=" + joinedGroups +
                ", currentCategoryStreaks=" + currentCategoryStreaks +
                ", bestCategoryStreaks=" + bestCategoryStreaks +
                ", profilePic='" + profilePic + '\'' +
                '}';
    }
}
