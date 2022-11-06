package edu.northeastern.numad22fa_team27.sticker_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;

import edu.northeastern.numad22fa_team27.sticker_messenger.models.IncomingMessage;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.MessageInfo;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.OutgoingMessage;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.StickerSendModel;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.StickerTypes;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.UserDAO;

public class FirebaseActivity extends AppCompatActivity {
    private final String TAG = FirebaseActivity.class.getSimpleName();
    private final String CHANNEL_ID = "STICKER_CHANNEL";
    private int notificationId = 0;

    private DatabaseReference mDatabase;
    private FriendsFragment friendsSendFragment;
    private UserDAO user;
    private TextView welcomeText;
    private ValueEventListener userChangeListener = null;

    private boolean isReceive = false;
    private final List<MessageCards> mCards = new ArrayList<>();
    private RecyclerView lists;

    private Integer[] knownStickerElements;

    // sticker values
    Map<StickerTypes, Integer> StickerCounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_messenger);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up our ability to send push messages
        createNotificationChannel();

        // Set up the sticker send fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentStickerFriends);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(currentFragment).commit();

        // Precompute lists that we'll repeatedly use later
        knownStickerElements = new Integer[] {
                R.id.txt_sticker_one,
                R.id.txt_sticker_two,
                R.id.txt_sticker_three,
                R.id.txt_sticker_four,
                R.id.txt_sticker_five
        };

        StickerCounts = new HashMap<>();
        resetStickerCounter();

        // Set up our RecyclerView
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        lists = findViewById(R.id.id_rec_sticker);
        lists.setHasFixedSize(true);
        lists.setAdapter(new MessageInfo(mCards));
        lists.setLayoutManager(manager);

        //welcomeText = findViewById(R.id.txt_welcome);
        ImageView imgOne = findViewById(R.id.img_sticker_one);
        ImageView imgTwo = findViewById(R.id.img_sticker_two);
        ImageView imgThree = findViewById(R.id.img_sticker_three);
        ImageView imgFour = findViewById(R.id.img_sticker_four);
        ImageView imgFive = findViewById(R.id.img_sticker_five);

        imgOne.setImageResource(getDrawableFromEnum(StickerTypes.STICKER_1));
        imgTwo.setImageResource(getDrawableFromEnum(StickerTypes.STICKER_2));
        imgThree.setImageResource(getDrawableFromEnum(StickerTypes.STICKER_3));
        imgFour.setImageResource(getDrawableFromEnum(StickerTypes.STICKER_4));
        imgFive.setImageResource(getDrawableFromEnum(StickerTypes.STICKER_5));

        updateImages();

        promptLogin();

        // Callback to send stickers
        StickerSendModel viewModel = new ViewModelProvider(this).get(StickerSendModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            // Actually send the sticker
            trySendSticker(new OutgoingMessage(
                    new Date(),
                    item.first,
                    StickerTypes.valueOf(item.second)
            ));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Destroy our event listener if the activity is over
        if (mDatabase != null && userChangeListener != null) {
            mDatabase.removeEventListener(userChangeListener);
        }
    }

    private void createNotificationChannel() {
        // Similar to the official documentation at
        // https://developer.android.com/develop/ui/views/notifications/channels#java

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Only proceed if API is 26+ due to incompatibility
            return;
        }

        String description = getString(R.string.sticker_notification_channel_description);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.sticker_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(description);

        // Register the channel with the system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void pushStickerUpdate(IncomingMessage sticker) {
        Bitmap stickerBitmap = BitmapFactory.decodeResource(getResources(), getDrawableFromEnum(sticker.getSticker()));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Sticker!")
                .setContentText(String.format("%s just gave you a new %s sticker!", sticker.getSourceUser(), sticker.getSticker()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(stickerBitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(stickerBitmap)
                        .bigLargeIcon(null));

        // Actually push the notification
        getSystemService(NotificationManager.class).notify(notificationId++, notificationBuilder.build());
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
                String providedUsername = usernameText.getText().toString();
                if (Util.stringIsNullOrEmpty(providedUsername)) {
                    usernameText.setError("Username can't be empty");
                } else {
                    // get the user
                    mDatabase.child("users").child(providedUsername)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    user = userFromSnapshot(snapshot);

                                    // Initial draw call to show stickers
                                    populateRecycler();
                                    stickerSentCounter();
                                    updateImages();

                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Welcome Back!",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    changeListener();
                                } else {
                                    addUser(providedUsername);
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
     */
    private void addUser(String username) {
        mDatabase.child("users").child(username).setValue(new UserDAO(username))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            String.format("Successfully signed up with username %s!", username),
                            Toast.LENGTH_SHORT
                    ).show();

                    // Set our user and listen for changes
                    user = new UserDAO(username);
                    changeListener();
                }).addOnFailureListener(e -> {
                    Toast.makeText(
                        this,
                        "Failed to sign up. Please retry",
                            Toast.LENGTH_LONG
                    ).show();

                    // Try again with a different user
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

                // Determine the number of new stickers
                List<IncomingMessage> newStickers = new ArrayList<>();
                if (canReplace(user.incomingMessages, userDelta.incomingMessages)) {
                    if (userDelta.incomingMessages == null) {
                        Log.v(TAG, "Data consistency error - DB has been wiped");
                    } else if (userDelta.incomingMessages != null && user.incomingMessages == null) {
                        newStickers = userDelta.incomingMessages;
                    } else if (userDelta.incomingMessages.size() > user.incomingMessages.size()) {
                        userDelta.incomingMessages.removeAll(user.incomingMessages);
                        newStickers = userDelta.incomingMessages;
                    } else {
                        Log.v(TAG, "Data consistency error - DB has less received stickers than we have");
                    }
                }

                // Push notify the number of new stickers
                if (!newStickers.isEmpty()) {
                    for (IncomingMessage sticker : newStickers) {
                        pushStickerUpdate(sticker);
                    }
                }

                populateRecycler();
                stickerSentCounter();
                updateImages();

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

    public void populateRecycler() {
        List<MessageCards> newCards = (isReceive)
                ? user.incomingMessages.stream()
                    .map(m -> new MessageCards(m.getSticker(), "From: " + m.getSourceUser(), m.getDateSent().toString()))
                    .collect(Collectors.toList())
                : user.outgoingMessages.stream()
                    .map(m -> new MessageCards(m.getSticker(), "To: " + m.getDestUser(), m.getDateSent().toString()))
                    .collect(Collectors.toList());

        // Display results
        new Handler(Looper.getMainLooper()).post(() -> {
            mCards.clear();
            mCards.addAll(newCards);
            Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();
        });

    }

    /**
     * when the switch happens
     */
    public void switchView(View v) {
        isReceive = (!isReceive);
        populateRecycler();
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
     * Show a pop-up to select the sticker to send
     */
    public void sendStickerDialog(View v) {
        if (user == null || user.friends == null || user.friends.isEmpty()) {
            Toast.makeText(
                    getApplicationContext(),
                    "No friends to send a sticker to.",
                    Toast.LENGTH_SHORT).show();

            Log.e(TAG, "Tried to send to friends without (either) an initialized user or friends");
            return;
        }

        List<String> stickers = Stream.of(StickerTypes.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        // Build a fragment with our current friends list
        friendsSendFragment = FriendsFragment.newInstance(user.friends, stickers);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentStickerFriends, friendsSendFragment)
                .show(friendsSendFragment)
                .commit();
    }

    private List<IncomingMessage> marshalIncoming(DataSnapshot snapshot) {
        return StreamSupport.stream(snapshot.getChildren().spliterator(), false)
                .map(e -> new IncomingMessage(
                        (Date)e.child("dateSent").getValue(Date.class),
                        (String)e.child("sourceUser").getValue(String.class),
                        (StickerTypes)e.child("sticker").getValue(StickerTypes.class)
                ))
                .collect(Collectors.toList());
    }

    private List<OutgoingMessage> marshalOutgoing(DataSnapshot snapshot) {
        return StreamSupport.stream(snapshot.getChildren().spliterator(), false)
                .map(e -> new OutgoingMessage(
                        (Date)e.child("dateSent").getValue(Date.class),
                        (String)e.child("destUser").getValue(String.class),
                        (StickerTypes)e.child("sticker").getValue(StickerTypes.class)
                ))
                .collect(Collectors.toList());
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
                        incomingMessages = marshalIncoming(ds);
                        break;
                    }
                    case "outgoingMessages": {
                        outgoingMessages = marshalOutgoing(ds);
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

    private void updateImages() {
        int index = 0;
        for (StickerTypes s : StickerTypes.values()) {
            if (index >= knownStickerElements.length) {
                break;
            }
            TextView countBox = findViewById(knownStickerElements[index++]);
            countBox.setText(Integer.toString(StickerCounts.get(s)));
        }
    }

    private int getDrawableFromEnum(StickerTypes sticker) {
        switch (sticker) {
            case STICKER_1:
                return R.drawable.arcade_vectorportal;
            case STICKER_2:
                return R.drawable.baseball_vectorportal;
            case STICKER_3:
                return R.drawable.vinyl_vectorportal;
            case STICKER_4:
                return R.drawable.chicken_bucket_vectorportal;
            case STICKER_5:
                return R.drawable.cassette_vectorportal;
            default:
                // We don't know
                return R.drawable.ufo_vectorportal;
        }
    }

    private void resetStickerCounter() {
        for (StickerTypes s : StickerTypes.values()) {
            StickerCounts.put(s, 0);
        }
    }

    private void stickerSentCounter() {
        resetStickerCounter();

        for (OutgoingMessage om: user.outgoingMessages) {
            // count = (old count if present, else 0) + 1
            StickerTypes sticker = om.getSticker();
            StickerCounts.put(sticker, StickerCounts.getOrDefault(sticker, 0) + 1);
        }
    }
}