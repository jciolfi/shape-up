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
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutClickListener;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecAdapter;
import edu.northeastern.numad22fa_team27.workout.fragments.NewGroupChatFragment;
import edu.northeastern.numad22fa_team27.workout.models.ChatItem;
import edu.northeastern.numad22fa_team27.workout.models.ChatItemViewModel;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class WorkoutMessageActivity extends AppCompatActivity {

    //stored data variables
    private String[] friends;
    private String[] chats;
    private final List<MessageCard> cards = new ArrayList<>();
    private boolean showingSearch = false;

    //Activity elements
    private RecyclerView chatsRecycler;
    ProgressBar progressBar;
    NewGroupChatFragment chatFragment;

    //other variables
    private final String TAG = "WorkoutMessageActivity__";
    private ChatItem newChatQuery;
    Thread recThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_message);

        //initialize a list of firends

        setFriends();


        //RecyclerView
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        chatsRecycler = findViewById(R.id.rcv_chats);
        chatsRecycler.setHasFixedSize(true);
        chatsRecycler.setAdapter(new MessageAdapter(cards));
        chatsRecycler.setLayoutManager(manager);

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


        //hold off on thread for now
        /*recThread = new Thread(new RecommendationThread());
        recThread.start();*/
    }

    //sets the friend can split this up for comprehension
    private void setFriends() {
        //get information on the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firestore.collection("users").document(currentID);
        //this is code to get information on user
        reference.get().addOnCompleteListener(task -> {
            if(task.getResult().exists()) {
                Object object = task.getResult().get("friends");
                List<String> string = (List<String>) object;

                if (!string.isEmpty()) {
                    friends = new String[string.size()];
                    friends = string.toArray(friends);
                } else {
                    friends = new String[] {"Blank"};
                }

                chatFragment = new NewGroupChatFragment(friends);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.newMessageFragment, chatFragment, "newChat")
                        .hide(chatFragment)
                        .commit();

            } else {
                //Toast.makeText(ProfileActivity.this, "Couldn't fetch the profile for the user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecView(RecyclerView rv, List<Workout> dataset) {

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

        WorkoutClickListener clickListener = new WorkoutClickListener(dataset, activityLauncher);

        @SuppressLint("WrongConstant") RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setAdapter(new WorkoutRecAdapter(dataset, clickListener, true));
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