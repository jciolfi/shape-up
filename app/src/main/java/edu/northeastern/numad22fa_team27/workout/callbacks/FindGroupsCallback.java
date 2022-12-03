package edu.northeastern.numad22fa_team27.workout.callbacks;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;

public class FindGroupsCallback extends WorkoutCallback {
    private final List<GroupDAO> displayGroups;
    private final RecyclerView groupRV;

    public FindGroupsCallback(List<GroupDAO> displayGroups, RecyclerView groupRV) {
        this.displayGroups = displayGroups;
        this.groupRV = groupRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        displayGroups.clear();
        displayGroups.addAll(snapshot.toObjects(GroupDAO.class));
        Objects.requireNonNull(groupRV.getAdapter()).notifyDataSetChanged();
    }
}
