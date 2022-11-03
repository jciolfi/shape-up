package edu.northeastern.numad22fa_team27.sticker_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.UserDAO;

public class FirebaseActivity extends AppCompatActivity {
    private final String TAG = FirebaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private UserDAO user;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_messenger);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        welcomeText = findViewById(R.id.txt_welcome);

        /**
         * TODO:
         * [x] login flow (login or signup with username)
         * [x] add a friend (do we need a notion of a friend request? - assuming no for now)
         * [] incoming message listener (show notification)
         * [] button to send a sticker
         */
        promptLogin();
    }

    public void showTeamDetails(View v) {
        String message = "Team 27:\nBen, Fabian, Farzad, John";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Create an AlertDialog with field for users to enter username
     * If username exists, log in. If username doesn't exist, create account.
     */
    private void promptLogin() {
        final EditText usernameText = new EditText(this);
        AlertDialog loginDialog = new AlertDialog.Builder(this)
                .setTitle("Log in with your username")
                .setMessage("If you don't have an account, one will be made for you")
                .setView(usernameText)
                .setPositiveButton("Log in", null)
                .setCancelable(false)
                .create();

        // add listener for "Log in" button - leave dialog up if username unspecified
        loginDialog.setOnShowListener(dialogInterface -> {
            Button loginButton = loginDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            loginButton.setOnClickListener(view -> {
                if (Util.stringIsNullOrEmpty(usernameText.getText().toString())) {
                    usernameText.setError("Username can't be empty");
                } else {
                    // get the user
                    mDatabase.child("users").child(usernameText.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    user = snapshot.getValue(UserDAO.class);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Welcome Back!",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    addUser(usernameText.getText().toString(), true);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });

                    loginDialog.dismiss();
                }
            });
        });

        loginDialog.show();
    }

    /**
     * Add a fresh user entry for the given username
     * @param username the username for the user to insert
     * @param setThisUser if we're adding this user
     */
    private void addUser(String username, boolean setThisUser) {
        mDatabase.child("users").child(username).setValue(new UserDAO(username))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            String.format("Successfully signed up with username %s!", username),
                            Toast.LENGTH_SHORT
                    ).show();

                    if (setThisUser) {
                        user = new UserDAO(username);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(
                        this,
                        "Failed to sign up. Please retry",
                            Toast.LENGTH_LONG
                    ).show();
                    promptLogin();
                });
    }

    /**
     * Show a pop-up to enter the username of a friend
     */
    public void addFriendDialog(View v) {
        final EditText friendText = new EditText(this);
        AlertDialog addFriendDialog = new AlertDialog.Builder(this)
                .setTitle("Enter username of friend")
                .setView(friendText)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        addFriendDialog.setOnShowListener(dialogInterface -> {
            Button addButton = addFriendDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(view -> {
                if (Util.stringIsNullOrEmpty(friendText.getText().toString())) {
                    friendText.setError("Username can't be empty");
                } else {
                    tryAddFriend(friendText.getText().toString());
                    addFriendDialog.dismiss();
                }
            });
        });

        addFriendDialog.show();
    }

    /**
     * Add the given username to this user's friends if the username exists and isn't already a friend
     * @param username the username to try to add as a friend
     */
    private void tryAddFriend(String username) {
        mDatabase.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (user.friends != null && user.friends.contains(username)) {
                        Toast.makeText(
                            getApplicationContext(),
                            String.format("You're already friends with %s!", username),
                            Toast.LENGTH_LONG
                        ).show();
                    } else if (user.username.equals(username)) {
                        Toast.makeText(
                            getApplicationContext(),
                            "You can't be friends with yourself!",
                            Toast.LENGTH_LONG
                        ).show();
                    } else {
                        if (user.friends == null) {
                            user.friends = new ArrayList<>();
                        }
                        user.friends.add(username);

                        mDatabase.child("users").child(user.username).setValue(user)
                                .addOnSuccessListener(unused -> Toast.makeText(
                                    getApplicationContext(),
                                    String.format("Successfully added %s as a friend!", username),
                                    Toast.LENGTH_SHORT
                                ).show()).addOnFailureListener(e -> {
                                    user.friends.remove(username);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            String.format("Failed to add %s as a friend. Please try again.", username),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });
                    }
                } else {
                    Toast.makeText(
                        getApplicationContext(),
                        String.format("Couldn't find username \"%s\"", username),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}