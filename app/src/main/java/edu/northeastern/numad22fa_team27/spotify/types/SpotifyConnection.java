package edu.northeastern.numad22fa_team27.spotify.types;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Scanner;

public class SpotifyConnection {
    private static final String accountToken = "Basic OWVkNzAwMDBjZWNkNDcwZjgwYzQ3MWYzMDgxYzdmYTY6MjQ4MWJhZTU4NWNjNGZmNGI5ZDIzMjBlNzNhNzc0Zjc=";
    private static final String apiUrl = "https://api.spotify.com/v1";
    private static final String TAG = "SpotifyConnection__";
    private SpotifyToken token = null;

    /**
     * Convert an InputStream to a string for a JSON payload
     * @param in the InputStream payload
     * @return a String representation for the JSON payload
     */
    private String convertStreamToString(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    /**
     * Extract artist ID from JSON document
     * @param jObj JSON payload to parse
     * @return Artist ID if present in JSON payload, else null
     */
    private String getArtistIdFromSearch(JSONObject jObj) {
        try {
            JSONObject artist = (JSONObject)jObj
                    .getJSONObject("artists")
                    .getJSONArray("items")
                    .get(0);
            return artist.getString("id");
        } catch (JSONException e) {
            Log.e(TAG, "Could not extract artist ID from JSON");
        }
        return null;
    }

    /**
     * Extract track ID from JSON document
     * @param jObj JSON payload to parse
     * @return Track ID if present in JSON payload, else null
     */
    private String getTrackIdFromSearch(JSONObject jObj) {
        try {
            JSONObject track = (JSONObject)jObj
                    .getJSONObject("tracks")
                    .getJSONArray("items")
                    .get(0);
            return track.getString("id");
        } catch (JSONException e) {
            Log.e(TAG, "Could not extract artist ID from JSON");
        }
        return null;
    }

    /**
     * Report if we have a valid token to communicate with Spotify
     * @return True if token is valid, else false
     */
    public boolean isReady() {
        return token != null && !token.isExpired();
    }

    /**
     * Get a session token for subsequent transactions
     * @return True if token was successfully acquired, else false
     */
    public boolean Connect() {
        if (isReady()) {
            // We're already done and have a valid token
            return true;
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL("https://accounts.spotify.com/api/token").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", accountToken);

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
            this.token = new SpotifyToken(
                    jObj.getString("access_token"),
                    new Timestamp(System.currentTimeMillis()).getTime(),
                    Long.parseLong(jObj.getString("expires_in")) * 1000
            );
        } catch (Exception e) {
            this.token = new SpotifyToken(null, 0, 0);
            Log.e(TAG, String.format("Couldn't log in: %s", e));
            return false;
        }
        return true;
    }

    /**
     * Get the Spotify object ID associated with a object's name
     * @param query The name of the object to search for
     * @param query_type The type of the object that is being searched for
     * @return ID string if ID was successfully acquired, else null
     */
    public String SearchForId(String query, SpotifyQueryDatatype query_type) {
        // First, replace characters like whitespace with something URL-ready
        try {
            query = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, String.format("Could not convert string %s to URL format", query));
            return null;
        }

        // Actually get a response
        HttpURLConnection conn;
        InputStream in;
        int statusCode;
        try {
            conn = (HttpURLConnection) new URL(String.format("%s/search?q=%s&type=%s&limit=1&offset=0", apiUrl, query, query_type.name().toLowerCase(Locale.ROOT))).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", String.format("Bearer %s", token.accessToken));
            conn.setDoInput(true);

            conn.connect();

            // Get our needed data
            statusCode = conn.getResponseCode();
            in = conn.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, String.format("Couldn't search for \"%s\": %s", query, e));
            return null;
        }

        // Detailed logging if we get network error codes
        if (statusCode != 200) {
            // Something about the connection or query went terribly wrong here.
            Log.e(TAG, String.format("Search code status is %d unexpectedly", statusCode));
            return null;
        }

        // Try to parse the JSON payload
        try {
            JSONObject jObj = new JSONObject(convertStreamToString(in));
            switch (query_type) {
                case ARTIST:
                    return getArtistIdFromSearch(jObj);
                case TRACK:
                    return getTrackIdFromSearch(jObj);
                default:
                    break;
            }
        } catch (JSONException e) {
            Log.e(TAG, String.format("Could not read as JSON. Error: %s", e));
        }

        // Fallthrough case
        return null;
    }
}
