package edu.northeastern.numad22fa_team27.sticker_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.IncomingMessage;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.OutgoingMessage;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.StickerTypes;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.UserDAO;

public class FirebaseActivity extends AppCompatActivity {
    private final String TAG = FirebaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private UserDAO user;
    private TextView welcomeText;
    private ValueEventListener userChangeListener = null;

    private SendFragment send;
    private boolean showingSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_messenger);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //welcomeText = findViewById(R.id.txt_welcome);
        updateImages();



        // Make the send button pull up our fragment
        final ImageView searchButton = findViewById(R.id.img_send_message);
        searchButton.setOnClickListener(v -> {
            List<String> input;
            if (user != null) {
                input = user.friends;
            } else {
                input = new ArrayList<>();
            }

            send = new SendFragment(input);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .add(R.id.sendMessageFragment, send, "send")
                    .hide(send)
                    .commit();
            toggleFragment();
            //searchButton.setVisibility(View.GONE);
        });

        /**
         * TODO:
         * [x] login flow (login or signup with username)
         * [x] add a friend (do we need a notion of a friend request? - assuming no for now)
         * [] incoming message listener (show notification)
         * [x] button to send a sticker
         */
        promptLogin();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Destroy our event listener if the activity is over
        if (mDatabase != null && userChangeListener != null) {
            mDatabase.removeEventListener(userChangeListener);
        }
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
                                    user = userFromSnapshot(snapshot);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Welcome Back!",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    addUser(usernameText.getText().toString(), true);
                                }
                                changeListener();
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

    public void changeListener() {
        userChangeListener = mDatabase.child("users").child(user.username).addValueEventListener(new ValueEventListener() {

            /**
             * Check if we can merge new data into old data list
             * @param oldData
             * @param newData
             * @return
             */
            private boolean canReplace(List oldData, List newData) {
                return (oldData == null && newData != null) || (oldData != null && newData != null && !newData.equals(oldData));
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDAO userDelta = userFromSnapshot(snapshot);

                if (canReplace(user.outgoingMessages, userDelta.outgoingMessages)) {
                    Log.v(TAG, "Data consistency error - DB and local mismatch on our sent messages");
                }

                // TODO - Notify user with push
                if (canReplace(user.incomingMessages, userDelta.incomingMessages)) {
                    if (userDelta.incomingMessages == null) {
                        Log.v(TAG, "Data consistency error - DB has been wiped");
                    } else if (userDelta.incomingMessages != null && user.incomingMessages == null) {
                        if (!userDelta.incomingMessages.isEmpty()) {
                            Log.v(TAG, String.format("We got %d new sticker(s)!", userDelta.incomingMessages.size()));
                        }
                    } else if (userDelta.incomingMessages.size() > user.incomingMessages.size()) {
                        Log.v(TAG, String.format("We got %d new sticker(s)!", userDelta.incomingMessages.size() - user.incomingMessages.size()));
                    } else {
                        Log.v(TAG, "Data consistency error - DB has less received stickers than we have");
                    }
                }

                if (canReplace(user.friends, userDelta.friends)) {
                    Log.v(TAG, "Data consistency error - DB and local mismatch on our friends list");
                }

                // In all cases, assume the DB is correct and update
                user = userDelta;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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

                    // TODO - Here for testing purposes. There should be a dialogue that triggers this
                    trySendSticker(new OutgoingMessage(
                            new Date(),
                            friendText.getText().toString(),
                            StickerTypes.STICKER_1
                    ));
                    addFriendDialog.dismiss();
                }
            });
        });

        addFriendDialog.show();
    }

    /**
     * Show a pop-up to select the sticker to send
     */
    public void sendMessageDialog(View v) {
        final EditText friendText = new EditText(this);

        AlertDialog addFriendDialog = new AlertDialog.Builder(this)
                .setTitle("Select which sticker and to which user you would like to send it to")
                .setView(friendText)
                .setPositiveButton("send", null)
                .setNegativeButton("Cancel", null)
                .create();

        addFriendDialog.setOnShowListener(dialogInterface -> {
            Button addButton = addFriendDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(view -> {
                if (Util.stringIsNullOrEmpty(friendText.getText().toString())) {
                    friendText.setError("Username can't be empty");
                } else {
                    tryAddFriend(friendText.getText().toString());

                    // TODO - Here for testing purposes. There should be a dialogue that triggers this
                    trySendSticker(new OutgoingMessage(
                            new Date(),
                            friendText.getText().toString(),
                            StickerTypes.STICKER_1
                    ));
                    addFriendDialog.dismiss();
                }
            });
        });

        addFriendDialog.show();
    }

    private UserDAO userFromSnapshot(@NonNull DataSnapshot snapshot) {
        List<String> friends = new ArrayList<>();
        List<IncomingMessage> incomingMessages = new ArrayList<>();
        List<OutgoingMessage> outgoingMessages = new ArrayList<>();
        for(DataSnapshot ds : snapshot.getChildren()) {
            String key = Objects.requireNonNull(ds.getKey());
            try {
                switch (key) {
                    case "friends": {
                        friends = (List<String>) ds.getValue();
                        break;
                    }
                    case "incomingMessages": {
                        incomingMessages = (List<IncomingMessage>) ds.getValue();
                        break;
                    }
                    case "outgoingMessages": {
                        outgoingMessages = (List<OutgoingMessage>) ds.getValue();
                        break;
                    }
                    default:
                        break;
                }
            } catch (Exception e) {
                // Creation of lists failed - this would happen if data present cannot be marshalled
                // into expected datatype.
                Log.e(TAG, String.format("Could not load data in DAO field %s", key));
            }
        }

        return new UserDAO(
                snapshot.getKey(),
                friends,
                incomingMessages,
                outgoingMessages
        );
    }

    private void trySendSticker(OutgoingMessage message) {
        mDatabase.child("users").child(message.getDestUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserDAO stickerRecipient = userFromSnapshot(snapshot);
                    stickerRecipient.incomingMessages.add(new IncomingMessage(message, user.username));

                    // Submit transaction
                    mDatabase.child("users").child(message.getDestUser()).setValue(stickerRecipient)
                            .addOnSuccessListener(unused -> {
                                // Record that we sent a sticker
                                user.outgoingMessages.add(message);

                                // Submit change to DB. This shouldn't fail, but handle it just in case
                                mDatabase.child("users").child(user.username).setValue(user)
                                        .addOnSuccessListener(u -> Toast.makeText(
                                                getApplicationContext(),
                                                String.format("Successfully sent a sticker to %s!", message.getDestUser()),
                                                Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> {
                                                user.outgoingMessages.remove(message);
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "Failed to record that we sent a sticker.",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                        });
                            }
                            ).addOnFailureListener(e -> Toast.makeText(
                                    getApplicationContext(),
                                    String.format("Failed to send a sticker to %s. Please try again.", message.getDestUser()),
                                    Toast.LENGTH_SHORT
                            ).show());


                } else {
                    // User not found
                    Toast.makeText(
                            getApplicationContext(),
                            String.format("Couldn't find friend \"%s\" to give sticker", message.getDestUser()),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    /**
     * Toggle the visibility of the search fragment
     */
    private void toggleFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        showingSend = !showingSend;
        if (showingSend) {
            transaction.show(send);
        } else {
            transaction.hide(send);
        }
        transaction.commit();
    }

    private void updateImages() {
//        ImageView imgOne = findViewById(R.id.img_sticker_one);
//        ImageView imgTwo = findViewById(R.id.img_sticker_two);
//        ImageView imgThree = findViewById(R.id.img_sticker_three);
//        ImageView imgFour = findViewById(R.id.img_sticker_four);
//        ImageView imgFive = findViewById(R.id.img_sticker_five);
//
//        imgOne.setImageResource(R.drawable.weights);
//        imgTwo.setImageResource(R.drawable.green);
//        imgThree.setImageResource(R.drawable.blue);
//        imgFour.setImageResource(R.drawable.red);
//        imgFive.setImageResource(R.drawable.yellow);
//
//        TextView txtOne = findViewById(R.id.txt_sticker_one);
//        TextView txtTwo = findViewById(R.id.txt_sticker_two);
//        TextView txtThree = findViewById(R.id.txt_sticker_three);
//        TextView txtFour = findViewById(R.id.txt_sticker_four);
//        TextView txtFive = findViewById(R.id.txt_sticker_five);
//
//        txtOne.setText("0");
//        txtTwo.setText("0");
//        txtThree.setText("0");
//        txtFour.setText("0");
//        txtFive.setText("0");


    }
}