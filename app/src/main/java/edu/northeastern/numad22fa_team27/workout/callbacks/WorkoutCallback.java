package edu.northeastern.numad22fa_team27.workout.callbacks;

import com.google.firebase.database.DataSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.WorkoutDAO;

public abstract class WorkoutCallback {
    public abstract void process(@NonNull DataSnapshot snapshot);
    public void processWorkout(List<WorkoutDAO> workouts) { }
}
