package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.fragments.GroupSearchFragment;
import edu.northeastern.numad22fa_team27.workout.fragments.SearchType;
import edu.northeastern.numad22fa_team27.workout.fragments.UserSearchFragment;
import edu.northeastern.numad22fa_team27.workout.fragments.WorkoutSearchFragment;

public class SearchActivity extends AppCompatActivity {
    private SearchType selectedSearch;
    private GroupSearchFragment groupSearchFragment;
    private UserSearchFragment userSearchFragment;
    private WorkoutSearchFragment workoutSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // initialize search instances
        groupSearchFragment = new GroupSearchFragment();
        userSearchFragment = new UserSearchFragment();
        workoutSearchFragment = new WorkoutSearchFragment();

        // set on click listeners for each search type
        final ImageView workoutImg = findViewById(R.id.icon_workout);
        workoutImg.setOnClickListener(view -> changeSearch(SearchType.WORKOUT));
        final ImageView userImg = findViewById(R.id.icon_user);
        userImg.setOnClickListener(view -> changeSearch(SearchType.USER));
        final ImageView groupImg = findViewById(R.id.icon_group);
        groupImg.setOnClickListener(view -> changeSearch(SearchType.GROUP));

        // default search to workout
        changeSearch(SearchType.WORKOUT);
    }

    /**
     * Change the view to the selected search type
     * @param newSearch the search view to bring up
     */
    private void changeSearch(SearchType newSearch) {
        // don't reload if same search clicked
        if (newSearch == selectedSearch) {
            Log.d("XXX", newSearch + " ||| " + selectedSearch);
            return;
        }

        // replace view with selected fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_workout_searches, getFragment(newSearch))
                .commit();

        // cache current search type
        selectedSearch = newSearch;
    }


    /**
     * return fragment associated with the type of search
     */
    private Fragment getFragment(SearchType searchType) {
        if (searchType.equals(SearchType.WORKOUT)) {
            return workoutSearchFragment;
        } else if (searchType.equals(SearchType.USER)) {
            return userSearchFragment;
        } else if (searchType.equals(SearchType.GROUP)) {
            return groupSearchFragment;
        }

        // default to workouts
        return workoutSearchFragment;
    }
}
