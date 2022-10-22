package edu.northeastern.numad22fa_team27.spotify;

import android.graphics.drawable.Icon;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.types.SongRecommendation;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyConnection;
import edu.northeastern.numad22fa_team27.spotify.types.SpotifyToken;


public class SpotifyActivity extends AppCompatActivity {
    private String TAG = "SpotifyActivity__";
    private final SpotifyConnection spotConnect = new SpotifyConnection();
    private List<SongRecommendation> songRecs = null;
    private RecyclerView lists;
    private TrackInfo artistInfo;
    private final ArrayList<Cards> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        // Set new auth token
        startBearerTokenThread();
        onCreatRec();
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

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView artistName;
        public TextView trackName;
        public ImageView artistImage;

        public Holder(View view) {
            super(view);
            views(view);
        }

        private void views(View view) {
            artistName = view.findViewById(R.id.artistname);
            trackName = view.findViewById(R.id.trackname);
            artistImage = view.findViewById(R.id.artistimage);
        }
    }

    public static class TrackInfo extends RecyclerView.Adapter<Holder> {

        private final ArrayList<Cards> list;

        public TrackInfo(ArrayList<Cards> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_cards, viewGroup, false);
            return new Holder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int i) {
            Cards cards = list.get(i);
            getInfo(h, cards);
        }

        private void getInfo(Holder h, Cards cards) {
            h.artistName.setText(cards.getArtistName());
            h.trackName.setText(cards.getTrackName());
            h.artistImage.setImageIcon(cards.getArtistImage());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public static class Cards {
        private final Icon artistImage;
        private final String artistName;
        private final String trackName;

        public Cards(Icon artistImage, String artistName, String trackName) {
            this.artistImage = artistImage;
            this.artistName = artistName;
            this.trackName = trackName;
        }

        public String getArtistName() {

            return artistName;
        }

        public String getTrackName() {

            return trackName;
        }

        public Icon getArtistImage() {

            return artistImage;
        }
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
