package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;

public class FindUsersCallback extends WorkoutCallback {
    private final List<Summarizeable> displayUsers;
    private final RecyclerView userRV;

    public FindUsersCallback(List<Summarizeable> displayUsers, RecyclerView userRV) {
        this.displayUsers = displayUsers;
        this.userRV = userRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        displayUsers.clear();

        for (DocumentSnapshot ds : snapshot) {
            UserDAO userDAO = ds.toObject(UserDAO.class);
            if (userDAO != null) {
                displayUsers.add(new User(userDAO, ds.getId()));
            }
        }

        Objects.requireNonNull(userRV.getAdapter()).notifyDataSetChanged();
    }
}
