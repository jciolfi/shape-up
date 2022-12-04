package edu.northeastern.numad22fa_team27.workout.models.DAO;

import androidx.core.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class UserDAO {
    public String username;
    public List<String> friends;
    public List<String> joinedGroups;
    public Map<String, Pair<Integer, LocalDate>> currentCategoryStreaks;
    public Map <String, Integer> bestCategoryStreaks;
    public String profilePic;

    public UserDAO() {}

    public UserDAO(User u) {
        this.username = u.getUsername();
        this.friends = u.getFriends();
        this.joinedGroups = u.getJoinedGroups().stream().map(String::valueOf).collect(Collectors.toList());
//        this.currentCategoryStreaks = u.getCurrentCategoryStreaks();
//        this.bestCategoryStreaks = u.getBestCategoryStreaks();
//        this.profilePic = u.getProfilePic;
    }

    @Override
    public String toString() {
        return "UserDAO{" +
                "username='" + username + '\'' +
                ", friends=" + friends +
                ", joinedGroups=" + joinedGroups +
                ", currentCategoryStreaks=" + currentCategoryStreaks +
                ", bestCategoryStreaks=" + bestCategoryStreaks +
                ", profilePic='" + profilePic + '\'' +
                '}';
    }
}
