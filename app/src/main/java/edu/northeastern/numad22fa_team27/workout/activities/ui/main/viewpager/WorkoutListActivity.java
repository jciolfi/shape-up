package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.MainActivity;
import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.SpotifyActivity;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.sticker_messenger.FirebaseActivity;

public class WorkoutListActivity extends AppCompatActivity {

    private final List<WorkoutCard> workouts = new ArrayList<>();
    private ViewPager2 workoutViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);


        workouts.add(new WorkoutCard(R.drawable.vinyl_vectorportal, "test 1", false));
        workouts.add(new WorkoutCard(R.drawable.baseball_vectorportal, "test 2", false));
        workouts.add(new WorkoutCard(R.drawable.arcade_vectorportal, "test 2", false));

        workoutViewPager = findViewById(R.id.vpg_workout);
        workoutViewPager.setAdapter(new PagerWorkoutAdapter(workouts));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_toolbar);
        bottomNav.setOnItemSelectedListener(navListener);
    }

    private NavigationBarView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_leaderboard:
                            Intent intent = new Intent(WorkoutListActivity.this, SpotifyActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_profile:
                            intent = new Intent(WorkoutListActivity.this, FirebaseActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_workout:
                            intent = new Intent(WorkoutListActivity.this, WorkoutListActivity.class);
                            startActivity(intent);
                            break;


                    }
                    return false;
                }
            };


}