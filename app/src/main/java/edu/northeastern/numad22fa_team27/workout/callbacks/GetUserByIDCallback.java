package edu.northeastern.numad22fa_team27.workout.callbacks;

import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;

public class GetUserByIDCallback extends WorkoutCallback {
    private final User user;

    public GetUserByIDCallback(User user) {
        this.user = user;
    }

    @Override
    public void processDocument(@NonNull DocumentSnapshot snapshot) {
        UserDAO userDAO = snapshot.toObject(UserDAO.class);
        if (userDAO != null) {
            this.user.setUserFromDAO(userDAO, snapshot.getId());
        }
    }
}
