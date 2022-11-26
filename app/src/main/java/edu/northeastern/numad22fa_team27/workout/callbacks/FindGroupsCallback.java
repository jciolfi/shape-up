package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

public class FindGroupsCallback extends WorkoutCallback {
    @Override
    public void process(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()) {
            Log.d("FindGroupsCallback", ds.toString());
        }
    }
}
