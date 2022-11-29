package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;

public class FindGroupsCallback extends WorkoutCallback {
    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        for (GroupDAO group : snapshot.toObjects(GroupDAO.class)) {
            Log.d("FINDGROUPSCALLBACK", group.toString());
        }
    }
}
