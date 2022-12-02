package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindWorkoutsCallback;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.WorkoutAdapter;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class WorkoutSearchActivity extends AppCompatActivity {
    private final String TAG = "WorkoutSearchActivity";
    private FirestoreService firestoreService;
    private Spinner categoriesDropdown;
    private RecyclerView workoutRV;
    // workouts returned from search view
    private final List<WorkoutDAO> workoutCache = new ArrayList<>();
    // workouts filtered on category
    private final List<WorkoutDAO> displayWorkouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_search);

        firestoreService = new FirestoreService();

        // populate categories dropdown
        categoriesDropdown = findViewById(R.id.dropdown_workout);
        List<String> workoutCategories = WorkoutCategory.listCategories(true);
        workoutCategories.add(0, "None");
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                workoutCategories);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesDropdown.setAdapter(categoriesAdapter);
        categoriesDropdown.setSelection(0);
        categoriesDropdown.setOnItemSelectedListener(new CategoriesListener());

        // add query listener to search view
        SearchView workoutSearch = findViewById(R.id.sv_workout);
        workoutSearch.setOnQueryTextListener(new WorkoutQueryListener());

        // set up workout recycler view
        workoutRV = findViewById(R.id.rv_workout);
        workoutRV.setHasFixedSize(true);
        workoutRV.setLayoutManager(new LinearLayoutManager(this));
        workoutRV.setAdapter(new WorkoutAdapter(displayWorkouts));
    }

    private class WorkoutQueryListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // get selected category
            WorkoutCategory selectedCategory = WorkoutCategory.toCategory(
                    (String)categoriesDropdown.getSelectedItem());

            // update displayWorkouts and workoutCache
            firestoreService.findWorkoutsByCriteria(query, null,
                    new FindWorkoutsCallback(workoutCache, displayWorkouts, selectedCategory, workoutRV));

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

    private class CategoriesListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // get selected category
            WorkoutCategory selectedCategory = WorkoutCategory.toCategory(
                    (String)categoriesDropdown.getSelectedItem());

            // filter workouts on selected category
            displayWorkouts.clear();
            if (selectedCategory == null) {
                displayWorkouts.addAll(workoutCache);
            } else {
                displayWorkouts.addAll(workoutCache.stream()
                        .filter(w -> w.categoriesPresent.contains(selectedCategory))
                        .collect(Collectors.toList()));
            }

            // notify workouts changed
            Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
