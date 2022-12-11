package edu.northeastern.numad22fa_team27.workout.models;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FriendsCard {
    private String url;
    private String userId;
    private String username;

    public FriendsCard(String url, String userId, String username) {
         this.url = url;
         this.userId = userId;
         this.username = username;
    }

    public FriendsCard() {}

    public String getImageView() {
        return url;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
