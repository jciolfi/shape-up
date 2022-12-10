package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageCard;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageClickListener;
import edu.northeastern.numad22fa_team27.workout.fragments.NewGroupChatFragment;
import edu.northeastern.numad22fa_team27.workout.models.ChatItem;
import edu.northeastern.numad22fa_team27.workout.models.ChatItemViewModel;
import edu.northeastern.numad22fa_team27.workout.models.DAO.ChatDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.Message;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.utilities.ChatUtil;
import edu.northeastern.numad22fa_team27.workout.utilities.UserUtil;

public class WorkoutMessageActivity extends AppCompatActivity {

    //stored data variables
    private String[][] friends;
    private List<String> usernames = new ArrayList<>();
    private List<String> chats = new ArrayList<>();
    private List<Message> cards;
    private List<ChatUtil> messagelisteners;
    private boolean showingSearch = false;

    private Message chatUpdate;

    //Activity elements
    private RecyclerView chatsRecycler;
    private FloatingActionButton newChatButton;
    ProgressBar progressBar;
    NewGroupChatFragment chatFragment;


    //other variables
    private final String TAG = "WorkoutMessageActivity__";
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_message);

        cards = new ArrayList<>();

        //initialize a list of friends
        //and initialize the fragment whe its ready
        setFriends();
        setChats();

        //Loading icon
        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.VISIBLE);

        //New chat fragment

        //floating action button
        newChatButton = findViewById(R.id.new_chat_button);
        newChatButton.setOnClickListener(v -> {
            toggleSearchFragment();
        });

        // for the fragment i think
        incomingInfo();

        // RecyclerView
        chatsRecycler = findViewById(R.id.rcv_chats);
        setupRecView(chatsRecycler, cards);
        progressBar.setVisibility(View.INVISIBLE);

        //
        messagelisteners = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private ListenerRegistration createChatListener(String chatId, RecyclerView rvToUpdate) {
        return firestore.collection(Constants.MESSAGES)
                .document(chatId)
                .addSnapshotListener((EventListener<DocumentSnapshot>) (snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    int index = chats.indexOf(chatId);
                    chatUpdate = cards.get(index);
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        synchronized(chatUpdate) {
                            chatUpdate = new Message(snapshot.toObject(ChatDAO.class), chatId);
                        }
                        cards.set(index, new Message(snapshot.toObject(ChatDAO.class), chatId));
                        rvToUpdate.getAdapter().notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
    }

    private void incomingInfo() {
        ChatItemViewModel viewModel = new ViewModelProvider(this).get(ChatItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            Log.v(TAG, "newChat");
            if (item.getChatId() == "null" && item.getName() == "null") {
                toggleSearchFragment();
                return;
            }

            // create the new message
            // TODO: Integrate Message and ChatDAO model
            ChatDAO cd = new ChatDAO();
            cd.title = item.getName();
            cd.members = item.getChatMembers();
            cd.messages = item.getChatHistory();

            firestore.collection(Constants.MESSAGES)
                    .document(item.getChatId())
                    .set(cd)
                    .addOnSuccessListener(documentReference -> {
                        chats.add(item.getChatId());
                        progressBar.setVisibility(View.VISIBLE);
                        for (String s : item.getChatMembers()) {
                            FirebaseFirestore
                                    .getInstance()
                                    .collection(Constants.USERS)
                                    .document(s.trim())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        progressBar.setVisibility(View.VISIBLE);
                                        UserDAO ud = documentSnapshot.toObject(UserDAO.class);
                                        if (ud.chats == null) {
                                            ud.chats = new ArrayList<>();
                                        }
                                        ud.chats.add(item.getChatId());

                                        firestore.collection(Constants.USERS)
                                                .document(s.trim())
                                                .set(ud)
                                                .addOnSuccessListener(unused -> {
                                                    // TODO: Success condition here
                                                });
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }).addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));;
                        }
                        progressBar.setVisibility(View.INVISIBLE);

                    });
            cards.add(new Message(item.getChatId(), item.getName(), item.getChatMembers(), item.getChatHistory()));
            chatsRecycler.getAdapter().notifyDataSetChanged();

            toggleSearchFragment();
        });
    }

    private void setChats() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        firestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firestore.collection(Constants.USERS).document(currentID);

        reference.get().addOnSuccessListener(documentSnapshot -> {
            User currentUser = UserUtil.getInstance().getUser();
            chats = currentUser.getChats();
            for (int i = 0; i < currentUser.getChats().size(); i++) {
                findChatInfo(i);
            }
        });
    }

    private void findChatInfo(int index) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String messageKey = chats.get(index);

        FirebaseFirestore.getInstance()
                .collection(Constants.MESSAGES)
                .document(messageKey.trim())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    ChatDAO chat = documentSnapshot.toObject(ChatDAO.class);

                    if (chat.messages != null && chat.members != null && chat.title != null) {
                        cards.add(new Message(messageKey, chat.title, chat.members, chat.messages));
                        chatsRecycler.getAdapter().notifyDataSetChanged();
                        createChatListener(messageKey.trim(), chatsRecycler);
                    }
                });
    }

    //sets the friend can split this up for comprehension
    private void setFriends() {
        //get information on the user
        FirebaseAuth user_auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection(Constants.USERS)
                .document(user_auth.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    User currUser = new User(snapshot.toObject(UserDAO.class), user_auth.getUid());
                    Map<String, String> lookupMap = new HashMap<>();

                    List<Task<DocumentSnapshot>> lookups = currUser.getFriends().stream()
                            .map(friendId -> firestore.collection(Constants.USERS)
                                    .document(friendId)
                                    .get())
                            .collect(Collectors.toList());

                    Tasks.whenAll(lookups).addOnSuccessListener(v -> {
                        for (int i = 0; i < lookups.size(); i++) {
                            Task<DocumentSnapshot> completedTask = lookups.get(i);
                            DocumentSnapshot result = completedTask.getResult();
                            lookupMap.put(currUser.getFriends().get(i), result.toObject(UserDAO.class).username);
                        }

                        chatFragment = new NewGroupChatFragment(user_auth.getUid(), lookupMap);
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                .add(R.id.newMessageFragment, chatFragment, "newChat")
                                .hide(chatFragment)
                                .commit();
                    });
                });
    }

    private void findUserName(int index) {
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS)
                .document(friends[0][index])
                .get()
                .addOnSuccessListener(ds-> {
                    UserDAO ud = ds.toObject(UserDAO.class);
                    usernames.add(ud.username);
                    friends[1][usernames.size() - 1] = usernames.get(usernames.size() - 1);
                }).addOnFailureListener(ds -> {
                    usernames.add("User Not Found");
                    friends[1][usernames.size() - 1] = usernames.get(usernames.size() - 1);
                });
    }


    private void setupRecView(RecyclerView rv, List<Message> dataset) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setAdapter(new MessageAdapter(dataset, setUpMClickListener(dataset)));
        rv.setLayoutManager(manager);

        Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
    }

    private MessageClickListener setUpMClickListener(List<Message> dataset) {
        // this is passed to the click listener that is  created
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        String workoutId = extras.getString("WorkoutId");
                        Boolean completedWorkout = extras.getBoolean("Success");
                        if (completedWorkout) {
                            Toast.makeText(this, String.format("Congrats on completing workout %s", workoutId), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Okay, maybe next time.", Toast.LENGTH_LONG).show();
                        }

                        // TODO: Update user
                    }
                });

        MessageClickListener clickListener = new MessageClickListener(dataset, activityLauncher);
        return clickListener;
    }

    /**
     * Toggle the visibility of the new chat fragment
     */
    private void toggleSearchFragment() {
        if(chatFragment == null) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        showingSearch = !showingSearch;
        if (showingSearch) {
            transaction.show(chatFragment);
            newChatButton.setVisibility(View.GONE);
        } else {
            transaction.hide(chatFragment);
            newChatButton.setVisibility(View.VISIBLE);

        }
        transaction.commit();
    }
}