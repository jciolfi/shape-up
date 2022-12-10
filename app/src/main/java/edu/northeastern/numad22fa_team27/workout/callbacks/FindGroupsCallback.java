package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.Group;

public class FindGroupsCallback extends WorkoutCallback {
    private final List<Summarizeable> displayGroups;
    private final RecyclerView groupRV;

    public FindGroupsCallback(List<Summarizeable> displayGroups, RecyclerView groupRV) {
        this.displayGroups = displayGroups;
        this.groupRV = groupRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        displayGroups.clear();
        for (DocumentSnapshot ds : snapshot) {
            GroupDAO groupDAO = ds.toObject(GroupDAO.class);
            if (groupDAO != null) {
                displayGroups.add(new Group(groupDAO, ds.getId()));
            }
        }

        Objects.requireNonNull(groupRV.getAdapter()).notifyDataSetChanged();
    }
}
