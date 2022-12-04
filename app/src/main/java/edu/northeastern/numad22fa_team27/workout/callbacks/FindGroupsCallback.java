package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;

public class FindGroupsCallback extends WorkoutCallback {
    private final List<GroupDAO> displayGroups;
    private final RecyclerView groupRV;
    private final TextView noResults;

    public FindGroupsCallback(List<GroupDAO> displayGroups, RecyclerView groupRV, TextView noResults) {
        this.displayGroups = displayGroups;
        this.groupRV = groupRV;
        this.noResults = noResults;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        displayGroups.clear();
        displayGroups.addAll(snapshot.toObjects(GroupDAO.class));

        // display message when no results returned
        if (displayGroups.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.INVISIBLE);
        }

        Objects.requireNonNull(groupRV.getAdapter()).notifyDataSetChanged();
    }
}
