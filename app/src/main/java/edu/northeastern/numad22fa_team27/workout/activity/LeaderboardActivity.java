package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.GetLeaderboardCallback;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import edu.northeastern.numad22fa_team27.workout.models.leaderboard.LeaderboardAdapter;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.NavigationBar;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class LeaderboardActivity extends AppCompatActivity {
    private final static String TAG = "LeaderboardActivity";
    private FirestoreService firestoreService;
    private final String[] prevCategory = new String[]{""};
    private List<String> categories;
    private RecyclerView leaderboardRV;
    private final List<UserDAO> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        firestoreService = new FirestoreService();

        // Set up nav bar
        //BottomNavigationView bottomNav = findViewById(R.id.bottom_toolbar);
        //bottomNav.setSelectedItemId(R.id.nav_leaderboard);
        //bottomNav.setOnItemSelectedListener(NavigationBar.setNavListener(this));

        // set up categories dropdown
        Spinner categoryDropdown = findViewById(R.id.dropdown_leaderboard_category);
        categories = WorkoutCategory.listCategories(true, true);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryDropdown.setAdapter(categoryAdapter);
        categoryDropdown.setSelection(0);
        CategoryListener categoryListener = new CategoryListener();
        categoryDropdown.setOnItemSelectedListener(categoryListener);

        // set up recycler view
        leaderboardRV = findViewById(R.id.rv_leaderboard);
        leaderboardRV.setHasFixedSize(true);
        leaderboardRV.setLayoutManager(new LinearLayoutManager(this));
        leaderboardRV.setAdapter(new LeaderboardAdapter(users, prevCategory));
    }

    private class CategoryListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            // don't do extra work if we don't need to (select same sort again)
            if (prevCategory[0].equals(categories.get(position))) {
                return;
            }

            // update leaderboard
            firestoreService.findStreaksLeaderboard(
                    WorkoutCategory.toCategory(categories.get(position)), new GetLeaderboardCallback(users, leaderboardRV));

            // update previous selected category to search on
            prevCategory[0] = categories.get(position);

            Objects.requireNonNull(leaderboardRV.getAdapter()).notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    }
}
