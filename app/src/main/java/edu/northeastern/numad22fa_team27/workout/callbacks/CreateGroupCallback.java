package edu.northeastern.numad22fa_team27.workout.callbacks;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.Group;

public class CreateGroupCallback extends WorkoutCallback {
    private final List<Group> userGroups;
    private final RecyclerView userGroupsRV;

    public CreateGroupCallback(List<Group> userGroups, RecyclerView userGroupsRV) {
        this.userGroups = userGroups;
        this.userGroupsRV = userGroupsRV;
    }

    @Override
    public void processDocument(@NonNull DocumentSnapshot snapshot) {
        // add new group
        GroupDAO groupDAO = snapshot.toObject(GroupDAO.class);

        if (groupDAO == null) {
            return;
        }

        userGroups.add(new Group(groupDAO, snapshot.getId()));

        // notify recycler view
        Objects.requireNonNull(userGroupsRV.getAdapter()).notifyItemInserted(userGroups.size()-1);
    }
}
