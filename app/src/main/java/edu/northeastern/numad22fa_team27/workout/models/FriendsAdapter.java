package edu.northeastern.numad22fa_team27.workout.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Holder;
import edu.northeastern.numad22fa_team27.workout.interfaces.IRecyclerViewCardsClickable;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsHolder> {

    private final IRecyclerViewCardsClickable recyclerViewCardsClickable;

    private List<FriendsCard> list;

    public FriendsAdapter(List<FriendsCard> list, IRecyclerViewCardsClickable recyclerViewCardsClickable) {
        this.list = list;
        this.recyclerViewCardsClickable = recyclerViewCardsClickable;
    }

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_card, parent, false);
        return new FriendsHolder(view, recyclerViewCardsClickable);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        String email = list.get(position).getUsername();
        String url = list.get(position).getImageView();

        holder.username.setText(email);
        Picasso.get()
                .load(url)
                .into(holder.friendProfilePic);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
