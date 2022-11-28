package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.WorkoutDAO;

public abstract class WorkoutCallback {
    public abstract void process(@NonNull DataSnapshot snapshot);
    public void processWorkouts(List<WorkoutDAO> workouts) {
        Log.w("WorkoutCallback",
                "Default processWorkout called. Workouts not being processed");
    }
}
