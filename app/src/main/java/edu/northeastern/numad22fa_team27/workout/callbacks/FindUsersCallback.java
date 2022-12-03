package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;

public class FindUsersCallback extends WorkoutCallback {
    private final List<UserDAO> users;
    private final RecyclerView userRV;

    public FindUsersCallback(List<UserDAO> users, RecyclerView userRV) {
        this.users = users;
        this.userRV = userRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        users.clear();
        users.addAll(snapshot.toObjects(UserDAO.class));
        Objects.requireNonNull(userRV.getAdapter()).notifyDataSetChanged();
    }
}
