package edu.northeastern.numad22fa_team27.workout.models.workout_search;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.navigation.NavigationBarView;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.activity.UserGroupsActivity;
import edu.northeastern.numad22fa_team27.workout.activity.LeaderboardActivity;
import edu.northeastern.numad22fa_team27.workout.activity.ProfileActivity;
import edu.northeastern.numad22fa_team27.workout.activity.SearchActivity;
import edu.northeastern.numad22fa_team27.workout.activity.SettingsActivity;

public class NavigationBar {

    public static NavigationBarView.OnItemSelectedListener setNavListener(Context context) {
        NavigationBarView.OnItemSelectedListener navListener =
                item -> {
                    switch (item.getItemId()) {
                        case R.id.nav_leaderboard:
                            Intent intent = new Intent(context, LeaderboardActivity.class);
                            context.startActivity(intent);
                            break;
                        case R.id.nav_profile:
                            intent = new Intent(context, SettingsActivity.class);
                            context.startActivity(intent);
                            break;
                        case R.id.nav_groups:
                            intent = new Intent(context, UserGroupsActivity.class);
                            context.startActivity(intent);
                            break;
                        case R.id.nav_workout:
                            intent = new Intent(context, ProfileActivity.class);
                            context.startActivity(intent);
                            break;
                    }
                    return false;
                };
        return navListener;
    }
}
