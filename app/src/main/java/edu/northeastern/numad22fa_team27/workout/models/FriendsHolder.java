package edu.northeastern.numad22fa_team27.workout.models;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.interfaces.IRecyclerViewCardsClickable;

public class FriendsHolder extends RecyclerView.ViewHolder{
    public TextView username;
    public ImageView friendProfilePic;

    public FriendsHolder(View view, IRecyclerViewCardsClickable recyclerViewCardsClickable) {
        super(view);
        views(view, recyclerViewCardsClickable);
    } 

    private void views(View view, IRecyclerViewCardsClickable recyclerViewCardsClickable) {
        username = (TextView) view.findViewById(R.id.friend_username);
        friendProfilePic = (ImageView) view.findViewById(R.id.friend_iv);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewCardsClickable != null) {
                    int pos = getBindingAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewCardsClickable.onItemClick(pos);
                    }
                }
            }
        });
    }
}
