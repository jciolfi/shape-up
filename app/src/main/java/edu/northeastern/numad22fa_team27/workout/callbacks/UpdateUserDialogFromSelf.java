package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UpdateUserDialogFromSelf extends WorkoutCallback {
    private final User currentUser;
    private final User otherUser;
    private final Button actionButton;
    private final FirestoreService firestoreService;
    private final Dialog userInfoDialog;

    public UpdateUserDialogFromSelf(User currentUser, User otherUser, Button actionButton, FirestoreService firestoreService, Dialog userInfoDialog) {
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
            // this should never happen
            return;
        }

        currentUser.setUserFromDAO(userDAO, snapshot.getId());

        /**
         * Logic overview:
         * - if self -> hide action button
         * - if friends -> change to remove
         * - if not friends and user has requested currentUser -> change to accept
         */
        if (currentUser.getUserID().equals(otherUser.getUserID())) {
            actionButton.setVisibility(View.INVISIBLE);
            return;
        } else {
            actionButton.setVisibility(View.VISIBLE);
            actionButton.setEnabled(true);
        }

        if (currentUser.getFriends().contains(otherUser.getUserID())) {
            actionButton.setText("Remove");
            actionButton.setOnClickListener(removeView -> {
                firestoreService.removeFriend(otherUser.getUserID());
                userInfoDialog.dismiss();
            });
        } else if (currentUser.getIncomingFriendRequests().contains(otherUser.getUserID())) {
            actionButton.setText("Accept");
            actionButton.setOnClickListener(addView -> {
                firestoreService.tryAcceptFriendRequest(otherUser.getUserID());
                userInfoDialog.dismiss();
            });
        } else {
            // handles: when we've already requested them, or none of the above is true (add them)
            firestoreService.getUserByID(otherUser.getUserID(),
                    new UpdateUserDialogFromOther(currentUser, otherUser, actionButton, firestoreService, userInfoDialog));
        }
    }
}
