package edu.northeastern.numad22fa_team27.workout.models;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsCard {
    private int imageView;
    private String username;

    public FriendsCard(int imageView, String username) {
         this.imageView = imageView;
         this.username = username;
    }

    public int getImageView() {
        return imageView;
    }

    public String getUsername() {
        return username;
    }
}
