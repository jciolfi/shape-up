package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutSearch.WorkoutAdapter;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutSearch.WorkoutItem;

public class FindWorkoutsCallback extends WorkoutCallback {
    private final List<WorkoutItem> workouts;

    public FindWorkoutsCallback(List<WorkoutItem> workouts) {
        this.workouts = workouts;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        workouts.clear();
        for (WorkoutDAO workout : snapshot.toObjects(WorkoutDAO.class)) {
            workouts.add(new WorkoutItem(workout.workoutName));
        }
    }
}
