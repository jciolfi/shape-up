package edu.northeastern.numad22fa_team27.spotify;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Scanner;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyConnection;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyQueryDatatype;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyToken;


public class SpotifyActivity extends AppCompatActivity {
    private String TAG = "SpotifyActivity__";
    private final SpotifyConnection spotConnect = new SpotifyConnection();

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
                String successMessage = "Successfully loaded Spotify Details!";
                Snackbar.make(findViewById(android.R.id.content), successMessage, Snackbar.LENGTH_SHORT).show();

                String artistId = spotConnect.SearchForId("Lana Del Rey", SpotifyQueryDatatype.ARTIST);
                Log.v(TAG, "Artist ID is " + artistId);

                String songId = spotConnect.SearchForId("Thunderstruck", SpotifyQueryDatatype.TRACK);
                Log.v(TAG, String.format("Track ID is %s", songId));
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
