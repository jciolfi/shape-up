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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyConnection;

import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.TrackInfo;


public class SpotifyActivity extends AppCompatActivity {
    private final String TAG = "SpotifyActivity__";
    private final SpotifyConnection spotConnect = new SpotifyConnection();
    private final AtomicBoolean isLoading = new AtomicBoolean(false);
    private final List<Cards> cards = new ArrayList<>();
    private RecyclerView lists;

    private Thread recThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        // Set up our RecyclerView
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        lists = findViewById(R.id.idRecV);
        lists.setHasFixedSize(isHasFixedSize());
        lists.setAdapter(new TrackInfo(cards));
        lists.setLayoutManager(manager);

        // Start function threads
        recThread = new Thread(new RecommendationThread());
        recThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            recThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "Could not join worker thread");
        }
    }

    private boolean isHasFixedSize() {
        return true;
    }

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
        ProgressBar loadingPB = findViewById(R.id.pb_loading);

        @Override
        public void run() {
            // Show progress bar
            loadingPB.setVisibility(View.VISIBLE);

            if (spotConnect.Connect()) {
                // Perform dummy lookup. Actual user data should go here
                List<Cards> newCards = Optional.ofNullable(spotConnect.performRecommendation(
                        new LinkedList<String>() {{  add("Lana Del Rey"); add("FKA Twigs"); }},
                        new LinkedList<String>() {{  add("rock"); add("pop");}},
                        new LinkedList<String>() {{  add("Take On Me"); }},
                        100,
                        150
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
            } else {
                String message = "Failed to Load Spotify Details.";
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Go Back", view -> onBackPressed())
                    .show();
            }

            // In all cases, stop indicating that we're loading
            loadingPB.setVisibility(View.INVISIBLE);
        }
    }
}
