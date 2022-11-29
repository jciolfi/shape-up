package edu.northeastern.numad22fa_team27.workout.callbacks;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;

public class FindWorkoutCallback implements WorkoutCallback {
    @Override
    public void process(@NonNull QuerySnapshot snapshot) {
        for (WorkoutDAO workout : snapshot.toObjects(WorkoutDAO.class)) {

        }
    }
}
