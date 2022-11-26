package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.WorkoutDAO;

public class FindWorkoutCallback extends WorkoutCallback {
    @Override
    public void process(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()) {
            Log.d("FindWorkoutCallback", ds.toString());
        }
    }

    @Override
    public void processWorkout(List<WorkoutDAO> workouts) {
        for (WorkoutDAO workout : workouts) {
            Log.d("FindWorkoutCallback", workout.toString());
        }
    }
}
