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
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyToken;


public class SpotifyActivity extends AppCompatActivity {
    private SpotifyToken token;

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
        token = null;
        GetBearerTokenThread bearerTokenThread = new GetBearerTokenThread();
        new Thread(bearerTokenThread).start();

        LoadingThread loadingThread = new LoadingThread();
        new Thread(loadingThread).start();
    }

    /**
     * Thread that queries to Spotify's API to get a token
     */
    private class GetBearerTokenThread implements Runnable {
        private final String loginUrl = "https://accounts.spotify.com/api/token";

        @Override
        public void run() {
            try {
                // create connection & set headers
                HttpURLConnection conn;
                conn = (HttpURLConnection) new URL(loginUrl).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Authorization",
                    "Basic OWVkNzAwMDBjZWNkNDcwZjgwYzQ3MWYzMDgxYzdmYTY6MjQ4MWJhZTU4NWNjNGZmNGI5ZDIzMjBlNzNhNzc0Zjc=");
                // necessary for POST requests
                conn.setDoOutput(true);

                // set form data
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes("grant_type=client_credentials");
                wr.flush();
                wr.close();

                // make request and set token
                conn.connect();
                InputStream in = conn.getInputStream();
                JSONObject jObj = new JSONObject(convertStreamToString(in));
                setToken(new SpotifyToken(
                        jObj.getString("access_token"),
                        new Timestamp(System.currentTimeMillis()).getTime(),
                        Long.parseLong(jObj.getString("expires_in")) * 1000));

                String successMessage = "Successfully loaded Spotify Details!";
                Snackbar.make(findViewById(android.R.id.content), successMessage, Snackbar.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Stop LoadingThread
                token = new SpotifyToken(null, 0, 0);

                Log.e("SpotifyActivity", String.format("Couldn't log in: %s", e));
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
            while (getToken() == null) {
                loadingPB.setVisibility(View.VISIBLE);
            }
            loadingPB.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set bearer token for this class
     * @param token the Spotify bearer token
     */
    private void setToken(SpotifyToken token) {
        this.token = token;
    }

    /**
     * Retrieve the Spotify bearer token for this class
     * @return a SpotifyToken containing the token information
     */
    private SpotifyToken getToken() {
        return token;
    }

    /**
     * Convert an InputStream to a string for a JSON payload
     * @param in the InputStream payload
     * @return a String representation for the JSON payload
     */
    private String convertStreamToString(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
