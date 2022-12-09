package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.app.Dialog;
import android.widget.Button;

import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UpdateUserDialogFromOther extends WorkoutCallback {
    private final User currentUser;
    private final User otherUser;
    private final Button actionButton;
    private final FirestoreService firestoreService;
    private final Dialog userInfoDialog;

    public UpdateUserDialogFromOther(User currentUser, User otherUser, Button actionButton, FirestoreService firestoreService, Dialog userInfoDialog) {
        this.currentUser = currentUser;
        this.otherUser = otherUser;
        this.actionButton = actionButton;
        this.firestoreService = firestoreService;
        this.userInfoDialog = userInfoDialog;
    }

    @Override
    public void processDocument(@NonNull DocumentSnapshot snapshot) {
        UserDAO userDAO = snapshot.toObject(UserDAO.class);
        if (userDAO == null) {
            return;
        }

        otherUser.setUserFromDAO(userDAO, snapshot.getId());

        /**
         * Logic overview:
         * - if currentUser has requested otherUser -> change to requested
         * - if not friends and incoming request present -> change to add
         */
        if (otherUser.getIncomingFriendRequests().contains(currentUser.getUserID())) {
            actionButton.setText("Requested");
            actionButton.setEnabled(false);
        } else {
            actionButton.setText("Add");
            actionButton.setOnClickListener(addView -> {
                firestoreService.tryRequestFriend(otherUser.getUserID());
                userInfoDialog.dismiss();
            });
        }
    }
}
