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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.types.SongRecommendation;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyConnection;

import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.TrackInfo;


public class SpotifyActivity extends AppCompatActivity {
    private final String TAG = "SpotifyActivity__";
    private final SpotifyConnection spotConnect = new SpotifyConnection();
    private RecyclerView lists;
    private TrackInfo artistInfo;
    private ArrayList<Cards> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        // Set new auth token
        startBearerTokenThread();
        onCreatRec();

        TestThread test = new TestThread();
        new Thread(test).start();
    }

    private void artistManag(RecyclerView.LayoutManager manager) {
        artistInfo = new TrackInfo(cards);
        lists.setAdapter(artistInfo);
        lists.setLayoutManager(manager);
    }

    private boolean isHasFixedSize() {
        return true;
    }

    private void listName(String artistName, String trackName, Icon artistImage) {
        cards.add(0, new Cards(artistImage, artistName, trackName));
        artistInfo.notifyItemInserted(0);
    }

    private void onCreatRec() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        lists = findViewById(R.id.idRecV);
        lists.setHasFixedSize(isHasFixedSize());
        artistManag(manager);
    }

    private Icon getImageFromUrl(String imageURL) {
        try {
            URL url = new URL(imageURL);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return Icon.createWithAdaptiveBitmap(image);
        } catch(IOException e) {
            System.out.println(e);
        }

        // TODO - need a default "broken image" icon
        return null;
    }

    /**
     * Start thread to get bearer token for authentication
     */
    private void startBearerTokenThread() {
        GetBearerTokenThread bearerTokenThread = new GetBearerTokenThread();
        new Thread(bearerTokenThread).start();

        TestThread loadingThread = new TestThread();
        new Thread(loadingThread).start();
    }

    /**
     * Thread that queries to Spotify's API to get a token
     */
    private class GetBearerTokenThread implements Runnable {

        @Override
        public void run() {
            if (spotConnect.Connect()) {
                // Tell the user we can run recommendations
                String successMessage = "Successfully loaded Spotify Details!";
                Snackbar.make(findViewById(android.R.id.content), successMessage, Snackbar.LENGTH_SHORT).show();

                // Perform dummy lookup. Actual user data should go here
                List<SongRecommendation> songRecs = spotConnect.performRecommendation(
                    new LinkedList<String>() {{  add("Lana Del Rey"); add("FKA Twigs"); }},
                    new LinkedList<String>() {{  add("rock"); add("pop");}},
                    new LinkedList<String>() {{  add("Take On Me"); }},
       0,
         0
                );

                // Dummy result reporting. Should go into UI elements
                if (songRecs != null) {
                    List<Cards> newCards = songRecs.stream()
                            .map(rec -> new Cards(getImageFromUrl(rec.getImageMedium()), rec.getArtistName(), rec.getTrackName()))
                            .collect(Collectors.toList());

                    new Handler(Looper.getMainLooper()).post(() -> {
                        cards.clear();
                        cards.addAll(newCards);
                        artistInfo.notifyDataSetChanged();
                    });


                } else {
                    Log.e(TAG, "No recommendations!");
                }
            } else {
                // Stop LoadingThread
                String message = "Failed to Load Spotify Details.";
                Snackbar failedGetToken = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
                failedGetToken.setAction("Go Back", view -> onBackPressed());
                failedGetToken.show();
            }
        }
    }


    /**
     * Thread that displays loading icon while no bearer token set
     */
    private class TestThread implements Runnable {
        @Override
        public void run() {
            ProgressBar loadingPB = findViewById(R.id.pb_loading);
            while (!spotConnect.isReady()) {
                loadingPB.setVisibility(View.VISIBLE);
            }
            loadingPB.setVisibility(View.INVISIBLE);
        }
    }
}
