package edu.northeastern.numad22fa_team27.workout.callbacks;

import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.Group;

public class FindGroupByIDCallback extends WorkoutCallback {
    private final Group group;

    public FindGroupByIDCallback(Group group) {
        this.group = group;
    }

    @Override
    public void processDocument(@NonNull DocumentSnapshot snapshot) {
        GroupDAO groupDAO = snapshot.toObject(GroupDAO.class);
        if (groupDAO != null) {
            this.group.setFromGroupDAO(groupDAO, snapshot.getId());
        }
    }
}
