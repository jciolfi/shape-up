package edu.northeastern.numad22fa_team27.spotify.spotifyviews;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;

public class TrackInfo extends RecyclerView.Adapter<Holder> {

    private final List<Cards> list;

    public TrackInfo(List<Cards> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_cards, viewGroup, false));
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