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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageCard;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageClickListener;
import edu.northeastern.numad22fa_team27.workout.fragments.NewGroupChatFragment;
import edu.northeastern.numad22fa_team27.workout.models.ChatItem;
import edu.northeastern.numad22fa_team27.workout.models.ChatItemViewModel;
import edu.northeastern.numad22fa_team27.workout.models.Message;

public class WorkoutMessageActivity extends AppCompatActivity {

    //stored data variables
    private String[][] friends;
    private List<String> usernames = new ArrayList<>();
    private List<String> chats;
    private List<Message> cards;
    private boolean showingSearch = false;

    //Activity elements
    private RecyclerView chatsRecycler;
    ProgressBar progressBar;
    NewGroupChatFragment chatFragment;

    //other variables
    private final String TAG = "WorkoutMessageActivity__";
    private Message newChatQuery;
    Thread recThread;
    FirebaseFirestore firestore;
    private String newChatId = "";
    private List<String> eachMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_message);
        cards = new ArrayList<>();

        //initialize a list of firends
        //and initialize the fragment whe its ready
        setFriends();
        setChats();

        //newMessage initializer
        //newMessage = new Message("Unknown", "", new ArrayList<>());

        //Loading icon
        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.INVISIBLE);

        //New chat fragment

        //floating action button
        FloatingActionButton newChatButton = findViewById(R.id.fab_new_chat);
        newChatButton.setOnClickListener(v -> {
            toggleSearchFragment(newChatButton);
        });


        //for the fragment i think
        ChatItemViewModel viewModel = new ViewModelProvider(this). get(ChatItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            Log.v(TAG, "newChat");
            if (item.getChatId() == "null" && item.getName() == "null") {
                toggleSearchFragment(newChatButton);
                return;
            }
            newChatQuery = item;
            //create the new message
            Map<String, Object> newMessage = new HashMap<>();
            newMessage.put("title", item.getName());
            newMessage.put("members", item.getChatMembers());
            newMessage.put("messages", item.getChatHistory());

            //public items to be set for the on succes listener because the don't allow var
            eachMember = item.getChatMembers();
            
            firestore.collection("messages")
                    .add(newMessage)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                            newChatId = documentReference.getId();
                            chats.add(newChatId.trim());

                            for (String s : eachMember) {
                                FirebaseFirestore
                                        .getInstance()
                                        .collection("users")
                                        .document(s.trim())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        List<String> userChats = new ArrayList<>();
                                        try {
                                            userChats = (List<String>) documentSnapshot
                                                    .getData()
                                                    .get("chats");
                                        } catch (NullPointerException e) {
                                            //don't need to do anything will create a new chat
                                        }
                                        userChats.add(newChatId);
                                        Map<String, Object> newInput = new HashMap<>();
                                        newInput.put("chats", userChats);

                                        firestore.collection("users")
                                                .document(s.trim())
                                                .set(newInput, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });;
                            }
                        }
                    });
            cards.add(new Message(newChatId, item.getName(), item.getChatMembers(), item.getChatHistory()));
            chatsRecycler.getAdapter().notifyDataSetChanged();

            toggleSearchFragment(newChatButton);
        });

        //RecyclerView
        chatsRecycler = findViewById(R.id.rcv_chats);
        setupRecView(chatsRecycler, cards);



        //hold off on thread for now
        /*recThread = new Thread(new RecommendationThread());
        recThread.start();*/
    }

    private void setChats() {
        //get information on the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        firestore = FirebaseFirestore.getInstance();

        //cards.add(new Message("unknown", "test" + 2, new ArrayList<>(), new ArrayList<>()));

        DocumentReference reference = firestore.collection("users").document(currentID);
        //this is code to get information on user
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //if (task.getResult().exists()) {
                    Object object = documentSnapshot.getData().get("chats"); //task.getResult().get("chats");
                    List<String> string = (List<String>) object;
                    try {
                        if (string != null || !string.isEmpty()) {
                            chats = string;
                            for (int i = 0; i < chats.size(); i++) {
                                findChatInfo(i);
                            }
                            //adapter.notifyItemInserted(insertIndex);

                            //chatsRecycler.setAdapter(new MessageAdapter(cards, setUpMClickListener(cards)));
                            //setupRecView(chatsRecycler,cards);
                        }
                    } catch (NullPointerException e) {
                        
                    }
                /*} else {
                    Toast.makeText(WorkoutMessageActivity.this, "Couldn't fetch chat List", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private void findChatInfo(int index) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();

        //cards.add(new Message("unknown", "test" + 2, new ArrayList<>(), new ArrayList<>()));
        String messageKey = chats.get(index);

        DocumentReference reference =  FirebaseFirestore.getInstance().collection("messages").document(messageKey.trim());
        //this is code to get information on user
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //if (task.getResult().exists()) {
                List<Map<String,String>> messages = ( List<Map<String,String>>) documentSnapshot.getData().get("messages");
                List<String> members = (List<String>) documentSnapshot.getData().get("members");
                String title = (String) documentSnapshot.getData().get("title");

                if (messages != null && members != null && title != null) {
                    cards.add(new Message(messageKey, title, members, messages));
                    chatsRecycler.getAdapter().notifyDataSetChanged();
                }
                /*} else {
                    Toast.makeText(WorkoutMessageActivity.this, "Couldn't fetch chat List", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    //sets the friend can split this up for comprehension
    private void setFriends() {
        //get information on the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        firestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firestore.collection("users").document(currentID);
        //this is code to get information on user
        reference.get().addOnCompleteListener(task -> {
            if(task.getResult().exists()) {
                Object object = task.getResult().get("friends");
                List<String> string = (List<String>) object;

                boolean runRecur = false;
                //make sure the id and username are matched up
                if (!string.isEmpty()) {
                    friends = new String[2][string.size()];
                    friends[0] = string.toArray(friends[0]);
                    String[] friends1 = new String[string.size()];
                    for (int i = 0; i < string.size(); i++) {
                        friends1[i] = "blank";
                    }
                    friends[1] =  friends1;
                    runRecur = true;
                } else {
                    friends = new String[2][1];
                    friends[0] = new String[] {"Blank"};
                    friends[1] = new String[] {"Blank"};
                }

                chatFragment = new NewGroupChatFragment(currentID, friends);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.newMessageFragment, chatFragment, "newChat")
                        .hide(chatFragment)
                        .commit();

                //find the username for each userid
                if (runRecur){
                    for (int i = 0; i < friends[0].length; i++) {
                        findUserName(i);
                    }
                }
            } else {
                Toast.makeText(WorkoutMessageActivity.this, "Couldn't fetch friends List", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findUserName(int index) {

        DocumentReference newReference = FirebaseFirestore.getInstance().collection("users").document(friends[0][index]);
        //this is code to get information on user
        newReference.get().addOnCompleteListener(task-> {
            Object object = task.getResult().get("username");
            String userName = (String) object;
            if (object == null) {
                usernames.add("User Not Found");
            } else {
                usernames.add(userName);
            }
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
        //this is passed to the click listener that is  created
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
     * @param chatButton the add chat floating button at the bottom of the page
     */
    private void toggleSearchFragment(FloatingActionButton chatButton) {
        if(chatFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            showingSearch = !showingSearch;
            if (showingSearch) {

                transaction.show(chatFragment);
                chatButton.setVisibility(View.GONE);
            } else {
                transaction.hide(chatFragment);
                chatButton.setVisibility(View.VISIBLE);

            }
            transaction.commit();
        }
    }

    /**
     * Thread that queries to FireStore API to get a token
     */
    private class RecommendationThread implements Runnable {
        private boolean run = true;

        public void halt() {
            run = false;
        }

        @Override
        public void run() {
            // Show progress bar
            new Handler(Looper.getMainLooper()).post(() -> progressBar.setVisibility(View.VISIBLE));

            /*if (spotConnect.Connect()) {
                // Stop indicating that we're loading
                new Handler(Looper.getMainLooper()).post(() -> progressBar.setVisibility(View.INVISIBLE));

                while (run) {
                    if (searchQuery == null) {
                        // TODO - Need something more efficient than polling, like events
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Log.v(TAG, "Cannot sleep");
                        }
                        continue;
                    }

                    // Show progress bar
                    new Handler(Looper.getMainLooper()).post(() -> {
                        loadingPB.setVisibility(View.VISIBLE);
                        lists.setVisibility(View.INVISIBLE);
                    });

                    // Perform dummy lookup. Actual user data should go here
                    List<Cards> newCards = Optional.ofNullable(spotConnect.performRecommendation(
                                    searchQuery.getArtistNames(),
                                    searchQuery.getGenres(),
                                    searchQuery.getTrackNames(),
                                    searchQuery.getPopularity(),
                                    searchQuery.getTempo()
                            ))
                            .map(Collection::stream)
                            .orElseGet(Stream::empty)
                            .map(rec -> new Cards(getImageFromUrl(rec.getImageMedium()), rec.getArtistName(), rec.getTrackName()))
                            .collect(Collectors.toList());

                    // Display results
                    new Handler(Looper.getMainLooper()).post(() -> {
                        cards.clear();
                        cards.addAll(newCards);
                        Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();
                    });

                    searchQuery = null;

                    // Stop indicating that we're loading
                    new Handler(Looper.getMainLooper()).post(() -> {
                        loadingPB.setVisibility(View.INVISIBLE);
                        lists.setVisibility(View.VISIBLE);
                    });
                }
            } else {
                String message = "Failed to Load Spotify Details.";
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Go Back", view -> onBackPressed())
                        .show();

                // Stop indicating that we're loading
                new Handler(Looper.getMainLooper()).post(() -> loadingPB.setVisibility(View.INVISIBLE));
            }*/
        }
    }
}