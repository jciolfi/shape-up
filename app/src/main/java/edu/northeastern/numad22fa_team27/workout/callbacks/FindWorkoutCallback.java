package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;

public class FindWorkoutCallback extends WorkoutCallback {
    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        for (WorkoutDAO workout : snapshot.toObjects(WorkoutDAO.class)) {
            Log.d("FINDWORKOUTCALLBACK", workout.toString());
        }
    }
}
