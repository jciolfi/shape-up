package edu.northeastern.numad22fa_team27.spotify.spotifyviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.numad22fa_team27.R;

public class TrackInfo extends RecyclerView.Adapter<Holder> {

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