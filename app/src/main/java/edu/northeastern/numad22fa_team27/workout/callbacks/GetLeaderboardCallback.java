package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;

public class GetLeaderboardCallback extends WorkoutCallback {
    private final List<UserDAO> users;
    private final RecyclerView leaderboardRV;

    public GetLeaderboardCallback(List<UserDAO> users, RecyclerView leaderboardRV) {
        this.users = users;
        this.leaderboardRV = leaderboardRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        users.clear();
        users.addAll(snapshot.toObjects(UserDAO.class));

        Objects.requireNonNull(leaderboardRV.getAdapter()).notifyDataSetChanged();
    }
}
