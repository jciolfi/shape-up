package edu.northeastern.numad22fa_team27.spotify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyConnection;

import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.TrackInfo;


public class SpotifyActivity extends AppCompatActivity {
    private final String TAG = "SpotifyActivity__";
    private final SpotifyConnection spotConnect = new SpotifyConnection();
    private SearchItem searchQuery = null;
    private final List<Cards> cards = new ArrayList<>();
    private RecyclerView lists;
    ProgressBar loadingPB;
    Thread recThread;
    SearchFragment search;
    private boolean showingSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        // Set up our RecyclerView
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        lists = findViewById(R.id.idRecV);
        lists.setHasFixedSize(true);
        lists.setAdapter(new TrackInfo(cards));
        lists.setLayoutManager(manager);

        // Set up our loading icon
        loadingPB = findViewById(R.id.pb_loading);
        loadingPB.setVisibility(View.INVISIBLE);

        // Set up search fragment
        search = new SearchFragment();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.spotifySearchFragment, search, "search")
                .hide(search)
                .commit();

        // Make the search button pull up our fragment
        final FloatingActionButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            toggleSearchFragment();
            searchButton.setVisibility(View.GONE);
        });

        SearchItemViewModel viewModel = new ViewModelProvider(this).get(SearchItemViewModel.class);
        viewModel.getSelectedItem().observe(this, item -> {
            // Perform an action with the latest item data
            Log.v(TAG, "New data");

            searchQuery = item;

            // Hide the fragment and show the search button again
            toggleSearchFragment();
            searchButton.setVisibility(View.VISIBLE);
        });

        recThread = new Thread(new RecommendationThread());
        recThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Toggle the visibility of the search fragment
     */
    private void toggleSearchFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        showingSearch = !showingSearch;
        if (showingSearch) {
            transaction.show(search);
        } else {
            transaction.hide(search);
        }
        transaction.commit();
    }

    /**
     * Obtain an Icon from a URL
     * @param imageURL Network URL to image
     * @return Image in Icon format on success, else null
     */
    private Icon getImageFromUrl(String imageURL) {
        try {
            Bitmap image = BitmapFactory.decodeStream(new URL(imageURL).openConnection().getInputStream());
            return Icon.createWithAdaptiveBitmap(image);
        } catch(IOException e) {
            Log.v(TAG, String.format("Could not get image from URL, error: %s", e));
        }

        // TODO - need a default "broken image" icon
        return null;
    }

    /**
     * Thread that queries to Spotify's API to get a token
     */
    private class RecommendationThread implements Runnable {
        private boolean run = true;

        public void halt() {
            run = false;
        }

        @Override
        public void run() {
            // Show progress bar
            new Handler(Looper.getMainLooper()).post(() -> loadingPB.setVisibility(View.VISIBLE));

            if (spotConnect.Connect()) {
                // Stop indicating that we're loading
                new Handler(Looper.getMainLooper()).post(() -> loadingPB.setVisibility(View.INVISIBLE));

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
            }
        }
    }
}
