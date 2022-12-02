package edu.northeastern.numad22fa_team27.workout.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Holder;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsHolder> {

    private List<FriendsCard> list;

    public FriendsAdapter(List<FriendsCard> list) {this.list = list;}

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_card, parent, false);
        return new FriendsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        int resource = list.get(position).getImageView();
        String email = list.get(position).getUsername();

        holder.setData(resource, email);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
