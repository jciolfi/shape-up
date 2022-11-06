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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    //sticker values
    private int stickerOne = 0;
    private int stickerTwo = 0;
    private int stickerThree = 0;
    private int stickerFour = 0;
    private int stickerFive = 0;


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

        imgOne.setImageResource(R.drawable.arcade_vectorportal);
        imgTwo.setImageResource(R.drawable.baseball_vectorportal);
        imgThree.setImageResource(R.drawable.cassette_vectorportal);
        imgFour.setImageResource(R.drawable.chicken_bucket_vectorportal);
        imgFive.setImageResource(R.drawable.vinyl_vectorportal);

        updateImages();

        /**
         * TODO:
         * [x] login flow (login or signup with username)
         * [x] add a friend (do we need a notion of a friend request? - assuming no for now)
         * [] incoming message listener (show notification)
         * [x] button to send a sticker
         */
        promptLogin();

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
        // TODO: This is a dummy image, emulating a sticker lookup
        Bitmap stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arcade_vectorportal);

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

                                    //TODO: To who ever this chunc of code will update the
                                    //      recycler so this should be called in an ondataChange
                                    //      im not sure if this is the right one
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
                        changeListener();
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
        if (isReceive) {
            List<MessageCards> newCards = new ArrayList<>();
            List<IncomingMessage> inComing = user.incomingMessages;
            for (IncomingMessage im: inComing) {
                newCards.add(new MessageCards(im.getSticker(),
                        "From: " + im.getSourceUser(),im.getDateSent().toString()));
            }

            // Display results
            new Handler(Looper.getMainLooper()).post(() -> {
                mCards.clear();
                mCards.addAll(newCards);
                Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();
            });
        } else {
            List<MessageCards> newCards = new ArrayList<MessageCards>();
            List<OutgoingMessage> outgoing = user.outgoingMessages;
            for (OutgoingMessage im: outgoing) {
                newCards.add(new MessageCards(im.getSticker(),
                        "To: " + im.getDestUser(),im.getDateSent().toString()));
            }

            // Display results
            new Handler(Looper.getMainLooper()).post(() -> {
                mCards.clear();
                mCards.addAll(newCards);
                Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();
            });
        }

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
        List<String> stickers = new ArrayList<>();
        for (StickerTypes s : StickerTypes.values()) {
            stickers.add(s.name());
        }
        friendsSendFragment = FriendsFragment.newInstance(user.friends, stickers);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentStickerFriends, friendsSendFragment)
                .show(friendsSendFragment)
                .commit();
    }

    private List<IncomingMessage> marshalIncoming(DataSnapshot snapshot) {
        List<IncomingMessage> newIncoming = new ArrayList<>();
        for (DataSnapshot entry : snapshot.getChildren()) {
            newIncoming.add(new IncomingMessage(
                    (Date)entry.child("dateSent").getValue(Date.class),
                    (String)entry.child("sourceUser").getValue(String.class),
                    (StickerTypes)entry.child("sticker").getValue(StickerTypes.class)
            ));
        }
        return newIncoming;
    }

    private List<OutgoingMessage> marshalOutgoing(DataSnapshot snapshot) {
        List<OutgoingMessage> newOutgoing = new ArrayList<>();
        for (DataSnapshot entry : snapshot.getChildren()) {
            newOutgoing.add(new OutgoingMessage(
                    (Date)entry.child("dateSent").getValue(Date.class),
                    (String)entry.child("destUser").getValue(String.class),
                    (StickerTypes)entry.child("sticker").getValue(StickerTypes.class)
            ));
        }
        return newOutgoing;
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
        TextView txtOne = findViewById(R.id.txt_sticker_one);
        TextView txtTwo = findViewById(R.id.txt_sticker_two);
        TextView txtThree = findViewById(R.id.txt_sticker_three);
        TextView txtFour = findViewById(R.id.txt_sticker_four);
        TextView txtFive = findViewById(R.id.txt_sticker_five);

        txtOne.setText(Integer.toString(stickerOne));
        txtTwo.setText(Integer.toString(stickerTwo));
        txtThree.setText(Integer.toString(stickerThree));
        txtFour.setText(Integer.toString(stickerFour));
        txtFive.setText(Integer.toString(stickerFive));
    }

    private void stickerSentCounter() {
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        int count5 = 0;

        for (OutgoingMessage om: user.outgoingMessages) {
            StickerTypes sticker = om.getSticker();
            switch (sticker){
                case STICKER_1:
                    count1++;
                    break;
                case STICKER_2:
                    count2++;
                    break;
                case STICKER_3:
                    count3++;
                    break;
                case STICKER_4:
                    count4++;
                    break;
                case STICKER_5:
                    count5++;
                    break;
                default:
                    break;
            }
        }

        stickerOne = count1;
        stickerTwo = count2;
        stickerThree = count3;
        stickerFour = count4;
        stickerFive = count5;
    }
}