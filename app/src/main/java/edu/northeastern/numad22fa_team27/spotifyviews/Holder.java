package edu.northeastern.numad22fa_team27.spotifyviews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class Holder extends RecyclerView.ViewHolder {
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
