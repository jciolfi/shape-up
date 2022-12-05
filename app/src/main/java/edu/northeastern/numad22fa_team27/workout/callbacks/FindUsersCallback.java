package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;

public class FindUsersCallback extends WorkoutCallback {
    private final List<User> displayUsers;
    private final RecyclerView userRV;
    private final TextView noResults;

    public FindUsersCallback(List<User> displayUsers, RecyclerView userRV, TextView noResults) {
        this.displayUsers = displayUsers;
        this.userRV = userRV;
        this.noResults = noResults;
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

        // display message when no results returned
        if (displayUsers.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.INVISIBLE);
        }

        Objects.requireNonNull(userRV.getAdapter()).notifyDataSetChanged();
    }
}
