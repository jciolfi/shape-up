package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class GetGroupByIDCallback extends WorkoutCallback {
    private final Group group;
    private final String currentUserID;
    private final Button actionButton;
    private final View searchView;
    private final Dialog groupInfoDialog;
    private final FirestoreService firestoreService;

    public GetGroupByIDCallback(Group group, String currentUserID, Button actionButton, View searchView, Dialog groupInfoDialog, FirestoreService firestoreService) {
        this.group = group;
        this.currentUserID = currentUserID;
        this.actionButton = actionButton;
        this.searchView = searchView;
        this.groupInfoDialog = groupInfoDialog;
        this.firestoreService = firestoreService;
    }

    @Override
    public void processDocument(@NonNull DocumentSnapshot snapshot) {
        GroupDAO groupDAO = snapshot.toObject(GroupDAO.class);
        if (groupDAO != null) {
            this.group.setFromGroupDAO(groupDAO, snapshot.getId());
        }

        /** Logic overview
         * - if current user is in group -> change button to leave
         *      - if user is the admin, must transfer admin before leaving -> disable button
         * - if current user is not in group -> change button to join
         *      - if group is not accepting members -> disable button
         */

        // if user in group, change to leave group button
        if (group.getMembers().contains(currentUserID)) {
            actionButton.setText("Leave");

            if (group.getAdminID().equals(currentUserID)) {
                actionButton.setEnabled(false);
            } else {
                actionButton.setEnabled(true);
            }

            actionButton.setOnClickListener(v -> {
                AlertDialog leaveDialog = new AlertDialog.Builder(searchView.getContext())
                        .setTitle("Are you sure you want to leave this group?")
                        .setMessage("You may not be able to re-join in the future")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            firestoreService.leaveGroup(group.getGroupID());
                            groupInfoDialog.dismiss();
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .create();
                leaveDialog.show();
            });
        } else {
            actionButton.setText("Join");
            actionButton.setEnabled(true);

            if (!group.getAcceptingMembers()) {
                actionButton.setEnabled(false);
            }

            actionButton.setOnClickListener(v -> {
                AlertDialog joinDialog = new AlertDialog.Builder(searchView.getContext())
                        .setTitle("Are you sure you want to join this group?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            firestoreService.tryJoinGroup(group.getGroupID());
                            groupInfoDialog.dismiss();
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .create();
                joinDialog.show();
            });
        }
    }
}
