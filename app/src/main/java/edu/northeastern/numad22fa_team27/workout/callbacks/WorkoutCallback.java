package edu.northeastern.numad22fa_team27.workout.callbacks;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface WorkoutCallback {
    void process(@NonNull QuerySnapshot snapshot);
}
