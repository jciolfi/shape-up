package edu.northeastern.numad22fa_team27.workout.activity;

import static edu.northeastern.numad22fa_team27.Constants.USERS;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class FriendProfileActivity extends AppCompatActivity {

    private TextView friend_email;
    private ImageView friend_profilePic;
    private Button actionButton;
    private final FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private final FirestoreService firestoreService = new FirestoreService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        friend_email = findViewById(R.id.friendUsername);
        friend_profilePic = findViewById(R.id.friendProfilePic);
        actionButton = findViewById(R.id.friendActionBtn);

        extractInformation();
        setActionButton();

        // TODO
        // Fetch the friends workouts from the DB
    }

    private void extractInformation() {
        String username = getIntent().getStringExtra("USERNAME");
        String url = getIntent().getStringExtra("PROFILEPIC");
        friend_email.setText(username);
        if (url != null && !url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .resize(120, 120)
                    .into(friend_profilePic);
        }
    }

    private void setActionButton() {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            return;
        }

        /*
        Logic Overview:
        - If currentUser is selectedUser: hide/disable action button
        - If currentUser is friends with selectedUser: change to remove
        - If selectedUser has requested currentUser as friend: change to accept
        - If currentUser has requested selectedUser as friend: change to requested + disable
        - If currentUser & selectedUser aren't friends, no requests: change to request
         */

        String currentUserID = fbUser.getUid();
        String selectedUserID = getIntent().getStringExtra("USERID");


        // hide button for now so user doesn't see changing button state
        actionButton.setVisibility(View.INVISIBLE);
        actionButton.setEnabled(false);
        if (currentUserID.equals(selectedUserID)) {
            return;
        }

        firestoreDB.collection(USERS)
                .document(currentUserID)
                .get()
                .addOnSuccessListener(currUserSnapshot -> {
                    UserDAO currentUser = currUserSnapshot.toObject(UserDAO.class);
                    if (currentUser == null) {
                        return;
                    }

                    if (currentUser.friends.contains(selectedUserID)) {
                        actionButton.setText("Remove");
                        actionButton.setVisibility(View.VISIBLE);
                        actionButton.setEnabled(true);
                        actionButton.setOnClickListener(v -> {
                            firestoreService.removeFriend(selectedUserID);
                            onBackPressed();
                        });
                    } else if (currentUser.incomingFriendRequests.contains(selectedUserID)) {
                        actionButton.setText("Accept");
                        actionButton.setVisibility(View.VISIBLE);
                        actionButton.setEnabled(true);
                        actionButton.setOnClickListener(v -> {
                            firestoreService.tryAcceptFriendRequest(selectedUserID);
                            onBackPressed();
                        });
                    } else {
                        firestoreDB.collection(USERS)
                                .document(selectedUserID)
                                .get()
                                .addOnSuccessListener(selectedUserSnapshot -> {
                                    UserDAO selectedUser = selectedUserSnapshot.toObject(UserDAO.class);
                                    if (selectedUser == null) {
                                        return;
                                    }

                                    if (selectedUser.incomingFriendRequests.contains(currentUserID)) {
                                        actionButton.setText("Requested");
                                        actionButton.setVisibility(View.VISIBLE);
                                        actionButton.setEnabled(false);
                                    } else {
                                        actionButton.setText("Request");
                                        actionButton.setVisibility(View.VISIBLE);
                                        actionButton.setEnabled(true);
                                        actionButton.setOnClickListener(v -> {
                                            firestoreService.tryRequestFriend(selectedUserID);
                                            onBackPressed();
                                        });
                                    }
                                });
                    }
                });
    }
}