package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.TrackInfo;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.MessageInfo;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageCard;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageClickListener;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutClickListener;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecAdapter;
import edu.northeastern.numad22fa_team27.workout.fragments.NewGroupChatFragment;
import edu.northeastern.numad22fa_team27.workout.models.ChatItem;
import edu.northeastern.numad22fa_team27.workout.models.ChatItemViewModel;
import edu.northeastern.numad22fa_team27.workout.models.Message;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class WorkoutMessageActivity extends AppCompatActivity {

    //stored data variables
    private String[][] friends;
    private String[] chats;
    private List<Message> cards;
    private boolean showingSearch = false;

    //Activity elements
    private RecyclerView chatsRecycler;
    ProgressBar progressBar;
    NewGroupChatFragment chatFragment;

    //other variables
    private final String TAG = "WorkoutMessageActivity__";
    private ChatItem newChatQuery;
    Thread recThread;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_message);

        //initialize a list of firends
        //and initialize the fragment whe its ready
        setFriends();


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
            newChatQuery = item;
            toggleSearchFragment(newChatButton);
        });

        //RecyclerView
        cards = new ArrayList<>();
        List<String> chathist = new ArrayList<>();
        chathist.add("firstMessage");
        cards.add(new Message("test", new ArrayList<>(), chathist));
        chatsRecycler = findViewById(R.id.rcv_chats);

        setupRecView(chatsRecycler, cards);



        //hold off on thread for now
        /*recThread = new Thread(new RecommendationThread());
        recThread.start();*/
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
                    friends[0] = new String[] {"Blank"};
                    friends[1] = new String[] {"Blank"};
                }

                chatFragment = new NewGroupChatFragment(friends[1]);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.newMessageFragment, chatFragment, "newChat")
                        .hide(chatFragment)
                        .commit();

                /*for(int i = 0; i < string.size(); i++){
                    DocumentReference reference2 = firestore.collection("users").document(string.get(i));
                    //this is code to get information on user
                    reference2.get().addOnCompleteListener(task2 -> {
                        Object object2 = task.getResult().get("username");
                        String userName = (String) object2;
                        friends[1][i] = username;

                    });
                }*/
                if (runRecur)
                    recurUserName(0, friends[1].length);

            } else {
                //Toast.makeText(ProfileActivity.this, "Couldn't fetch the profile for the user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recurUserName(int index, int accum) {
        if (accum == 0) {
            //chatFragment = new NewGroupChatFragment(friends[1]);
            return;
        } else {
            /*String [] added = new String[index];
            for (int i = 0; i < soFar.length; i++) {
                added[i] = soFar[i];
            }*/
            //added[soFar.length] =

            DocumentReference reference = firestore.collection("users").document(friends[0][index]);
            //this is code to get information on user
            reference.get().addOnCompleteListener(task -> {
                Object object = task.getResult().get("username");
                String userName = (String) object;
                friends[1][index] = userName;
                recurUserName(index + 1, accum - 1);
            });

        }
    }

    private void setupRecView(RecyclerView rv, List<Message> dataset) {

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

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setAdapter(new MessageAdapter(dataset, clickListener));
        rv.setLayoutManager(manager);

        Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
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