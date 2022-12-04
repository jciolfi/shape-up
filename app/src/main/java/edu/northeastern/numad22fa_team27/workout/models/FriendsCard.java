package edu.northeastern.numad22fa_team27.workout.models;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsCard {
    private int profilePic;
    private String username;

    public FriendsCard(int imageView, String username) {
         this.profilePic = imageView;
         this.username = username;
    }

    public FriendsCard() {}

    public int getImageView() {
        return profilePic;
    }

    public String getUsername() {
        return username;
    }
}
