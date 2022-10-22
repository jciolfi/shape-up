package edu.northeastern.numad22fa_team27.spotify;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.types.SongRecommendation;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyConnection;


public class SpotifyActivity extends AppCompatActivity {
    private String TAG = "SpotifyActivity__";
    private final SpotifyConnection spotConnect = new SpotifyConnection();
    private List<SongRecommendation> songRecs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        // Set new auth token
        startBearerTokenThread();
    }

    /**
     * Start thread to get bearer token for authentication
     */
    private void startBearerTokenThread() {
        GetBearerTokenThread bearerTokenThread = new GetBearerTokenThread();
        new Thread(bearerTokenThread).start();

        LoadingThread loadingThread = new LoadingThread();
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
                setSongRecommendations(spotConnect.performRecommendation(
                        new LinkedList<String>() {{  add("Lana Del Rey"); add("FKA Twigs"); }},
                        new LinkedList<String>() {{  add("rock"); add("pop");}},
                        new LinkedList<String>() {{  add("Take On Me"); }},
           0,
             0
                    )
                );

                // Dummy result reporting. Should go into UI elements
                if (hasSongRecommendations()) {
                    for (SongRecommendation currRec : songRecs) {
                        Log.v(TAG, currRec.toString());
                    }
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

    private void setSongRecommendations(List<SongRecommendation> recs) {
        songRecs = recs;
    }

    private void resetSongRecommendations() {
        songRecs = null;
    }

    private boolean hasSongRecommendations() {
        return songRecs != null;
    }


    /**
     * Thread that displays loading icon while no bearer token set
     */
    private class LoadingThread implements Runnable {
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
