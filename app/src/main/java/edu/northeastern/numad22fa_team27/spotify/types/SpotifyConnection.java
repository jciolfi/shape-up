package edu.northeastern.numad22fa_team27.spotify.types;

import android.util.Log;

import org.json.JSONArray;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

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
    private String parseArtistIdFromSearch(JSONObject jObj) {
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
    private String parseTrackIdFromSearch(JSONObject jObj) {
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
     * Extract track recommendations from JSON document
     * @param jObj JSON payload to parse
     * @return List of song recommendations present on valid document. May be of length 0
     */
    private List<SongRecommendation> parseSongRecommendations(JSONObject jObj) {
        List<SongRecommendation> recs = new LinkedList<>();

        try {
            JSONArray allRecs = jObj.getJSONArray("tracks");

            for (int i = 0; i < allRecs.length(); i++) {
                JSONObject currRec = (JSONObject) allRecs.get(i);
                recs.add(new SongRecommendation(
                        currRec.getString("name").replace("\n", " "),
                        ((JSONObject)currRec.getJSONArray("artists").get(0)).getString("name"),
                        ((JSONObject)currRec.getJSONObject("album").getJSONArray("images").get(0)).getString("url"),
                        ((JSONObject)currRec.getJSONObject("album").getJSONArray("images").get(1)).getString("url"),
                        ((JSONObject)currRec.getJSONObject("album").getJSONArray("images").get(2)).getString("url")
                ));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Could not extract artist ID from JSON");
        }
        return recs;
    }

    /**
     * Given a URL, report the JSON payload associated with a GET request
     * @param requestUrl URL to get a JSON payload response from
     * @return JSON payload if present, else null
     */
    private JSONObject performGetRequest(String requestUrl) {
        // Actually get a response
        HttpURLConnection conn;
        InputStream in;
        int statusCode;
        try {
            conn = (HttpURLConnection) new URL(requestUrl).openConnection();
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
            Log.e(TAG, String.format("Couldn't search for \"%s\": %s", requestUrl, e));
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
            return new JSONObject(convertStreamToString(in));
        } catch (JSONException e) {
            Log.e(TAG, "Could not create JSON object from payload");
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

        String url = String.format("%s/search?q=%s&type=%s&limit=1&offset=0", apiUrl, query, query_type.name().toLowerCase(Locale.ROOT));
        JSONObject jObj = performGetRequest(url);
        if (jObj != null) {
            switch (query_type) {
                case ARTIST:
                    return parseArtistIdFromSearch(jObj);
                case TRACK:
                    return parseTrackIdFromSearch(jObj);
                default:
                    break;
            }
        }

        // Fallthrough case
        return null;
    }

    /**
     *
     * @param seedArtist List of artist names. seedArtist.size() + seedGenres.size() + seedTracks.size() <= 5
     * @param seedGenres List of song genres. seedArtist.size() + seedGenres.size() + seedTracks.size() <= 5
     * @param seedTracks List of track names. seedArtist.size() + seedGenres.size() + seedTracks.size() <= 5
     * @param targetPopularity Ideal song popularity, from 1 to 100. If 0, is ignored.
     * @param targetTempo Ideal song tempo. If 0, is ignored.
     * @return List of recommendations on success, or null on failure.
     */
    public List<SongRecommendation> performRecommendation(List<String> seedArtist, List<String> seedGenres, List<String> seedTracks, int targetPopularity, int targetTempo) {
        // Basic input validation
        if (targetPopularity > 100 || seedArtist.size() < 1 || seedGenres.size() < 1 || seedTracks.size() < 1) {
            Log.e(TAG, "Cannot use parameters provided");
            return null;
        }

        // Look up artist ID from name. If artist or tracks have more than 5 args, drop them.
        String artistIds = seedArtist.subList(0, Math.min(seedArtist.size(), 5)).stream()
                .map(name -> this.SearchForId(name, SpotifyQueryDatatype.ARTIST))
                .collect(Collectors.joining(","));
        String genres = String.join(",", seedGenres.subList(0, Math.min(seedGenres.size(), 5)));

        String trackIds = seedArtist.subList(0, Math.min(seedTracks.size(), 5)).stream()
                .map(name -> this.SearchForId(name, SpotifyQueryDatatype.TRACK))
                .collect(Collectors.joining(","));

        // Spotify will reject a query this long because it has too many args
        if (seedArtist.size() + seedGenres.size() + seedTracks.size() > 5) {
            return null;
        }

        // Form the request.
        String requestUrl;
        try {
            requestUrl  = String.format(
                    "%s/recommendations?seed_artists=%s&seed_genres=%s&seed_tracks=%s%s%s",
                    apiUrl,
                    URLEncoder.encode(artistIds, StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(genres, StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(trackIds, StandardCharsets.UTF_8.toString()),
                    (targetPopularity > 0) ? String.format("&target_popularity=%s", targetPopularity): "",
                    (targetTempo > 0) ? String.format("&target_tempo=%s", targetTempo): ""
            );
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "Could not convert user input string string to URL format");
            return null;
        }

        JSONObject jObj = performGetRequest(requestUrl);
        if (jObj != null) {
            return parseSongRecommendations(jObj);
        }
        return null;
    }
}