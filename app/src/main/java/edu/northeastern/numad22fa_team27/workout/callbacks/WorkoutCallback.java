package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class WorkoutCallback {
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        Log.w("WorkoutCallback", "QuerySnapshot processing not implemented");
    }
    public void processDocument(@NonNull DocumentSnapshot snapshot) {
        Log.w("WorkoutCallback", "DocumentSnapshot processing not implemented");
    }
}
