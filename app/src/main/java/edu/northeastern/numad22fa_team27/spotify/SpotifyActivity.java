package edu.northeastern.numad22fa_team27.spotify;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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

    private void startBearerTokenThread() {
        GetBearerTokenThread bearerTokenThread = new GetBearerTokenThread();
        new Thread(bearerTokenThread).start();
    }

    private class GetBearerTokenThread implements Runnable {
        private final String loginUrl = "https://accounts.spotify.com/api/token";

        @Override
        public void run() {
            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) new URL(loginUrl).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Authorization",
                        "Basic OWVkNzAwMDBjZWNkNDcwZjgwYzQ3MWYzMDgxYzdmYTY6MjQ4MWJhZTU4NWNjNGZmNGI5ZDIzMjBlNzNhNzc0Zjc=");
                // necessary for POST requests
                conn.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes("grant_type=client_credentials");
                wr.flush();
                wr.close();

                conn.connect();

                InputStream in = conn.getInputStream();

                JSONObject jObj = new JSONObject(convertStreamToString(in));
                Log.d("SpotifyActivity", jObj.toString());

                setToken(new SpotifyToken(
                        jObj.getString("access_token"),
                        new Timestamp(System.currentTimeMillis()).getTime(),
                        Long.parseLong(jObj.getString("expires_in")) * 1000));

            } catch (Exception e) {
                Log.e("SpotifyActivity", String.format("Couldn't log in: %s", e));
                // TODO: not sure what to do here, display error pop-up to user and redirect to home? retry?
            }
        }
    }

    private void setToken(SpotifyToken token) {
        this.token = token;
    }

    private String convertStreamToString(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
