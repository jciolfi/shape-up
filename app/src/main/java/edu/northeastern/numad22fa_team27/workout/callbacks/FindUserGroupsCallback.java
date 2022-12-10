package edu.northeastern.numad22fa_team27.workout.callbacks;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.models.user_groups.UserGroupsAdapter;

public class FindUserGroupsCallback extends WorkoutCallback {
    private final List<Group> userGroups;
    private final RecyclerView userGroupsRV;

    public FindUserGroupsCallback(List<Group> userGroups, RecyclerView userGroupsRV) {
        this.userGroups = userGroups;
        this.userGroupsRV = userGroupsRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        userGroups.clear();

        for (DocumentSnapshot ds : snapshot) {
            GroupDAO groupDAO = ds.toObject(GroupDAO.class);
            if (groupDAO != null) {
                userGroups.add(new Group(groupDAO, ds.getId()));
            }
        }

        // IMPORTANT: only call once to set up adapter. If the callback for querying for a user
        // groups needs to happen again, create another callback.
        userGroupsRV.setAdapter(new UserGroupsAdapter(userGroups));
    }
}
