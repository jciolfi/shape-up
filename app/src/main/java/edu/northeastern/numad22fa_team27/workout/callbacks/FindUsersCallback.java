package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;

public class FindUsersCallback extends WorkoutCallback {
    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        for (UserDAO user : snapshot.toObjects(UserDAO.class)) {
            Log.d("FINDUSERSCALLBACK", user.toString());
        }
    }
}
