package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.SearchItemViewModel;
import edu.northeastern.numad22fa_team27.spotify.SpotifyActivity;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.TrackInfo;
import edu.northeastern.numad22fa_team27.workout.fragments.NewGroupChatFragment;
import edu.northeastern.numad22fa_team27.workout.models.ChatItem;
import edu.northeastern.numad22fa_team27.workout.models.ChatItemViewModel;

public class WorkoutMessageActivity extends AppCompatActivity {

    //stored data variables
    private String[] friends;
    private String[] chats;
    private final List<Cards> cards = new ArrayList<>();
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
        String[] listOfFriends = new String[] {"user1", "user2"};


        //RecyclerView
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        chatsRecycler = findViewById(R.id.rcv_chats);
        chatsRecycler.setHasFixedSize(true);
        chatsRecycler.setAdapter(new TrackInfo(cards));
        chatsRecycler.setLayoutManager(manager);

        //Loading icon
        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.INVISIBLE);

        //New chat fragment
        chatFragment = new NewGroupChatFragment(listOfFriends);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.newMessageFragment, chatFragment, "newChat")
                .hide(chatFragment)
                .commit();

        //floating action button
        FloatingActionButton newChatButton = findViewById(R.id.fab_new_chat);
        newChatButton.setOnClickListener(v -> {
            toggleSearchFragment(newChatButton);
        });


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

    /**
     * Toggle the visibility of the new chat fragment
     */
    private void toggleSearchFragment(FloatingActionButton chatButton) {
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