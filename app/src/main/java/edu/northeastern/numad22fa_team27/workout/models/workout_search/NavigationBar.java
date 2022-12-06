package edu.northeastern.numad22fa_team27.workout.models.workout_search;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.SpotifyActivity;
import edu.northeastern.numad22fa_team27.workout.activity.ProfileActivity;

public class NavigationBar {

    public static NavigationBarView.OnItemSelectedListener setNavListener(Context context) {
        NavigationBarView.OnItemSelectedListener navListener =
                item -> {
                    switch (item.getItemId()) {
                        case R.id.nav_leaderboard:
                            Intent intent = new Intent(context, SpotifyActivity.class);
                            context.startActivity(intent);
                            break;
                        case R.id.nav_profile:
                            intent = new Intent(context, ProfileActivity.class);
                            context.startActivity(intent);
                            break;
                    }
                    return false;
                };
        return navListener;
    }
}
