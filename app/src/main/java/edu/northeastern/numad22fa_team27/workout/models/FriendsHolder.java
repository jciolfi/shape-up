package edu.northeastern.numad22fa_team27.workout.models;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import edu.northeastern.numad22fa_team27.R;

public class FriendsHolder extends RecyclerView.ViewHolder{
    public TextView username;
    public ImageView friendProfilePic;

    public FriendsHolder(View view) {
        super(view);
        views(view);
    } 

    private void views(View view) {
        username = (TextView) view.findViewById(R.id.friend_username);
        friendProfilePic = (ImageView) view.findViewById(R.id.friend_iv);
    }
}
