package edu.northeastern.numad22fa_team27.workout.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutClickListener;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecAdapter;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindWorkoutsCallback;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UniversalSearchFragment extends DialogFragment {
    private final String TAG = "WorkoutSearchActivity";
    private FirestoreService firestoreService;
    private RecyclerView workoutRV;

    // workouts returned from search view
    private final List<Workout> workoutCache = new ArrayList<>();
    // workouts filtered on category
    private final List<Workout> displayWorkouts = new ArrayList<>();
    private TextView noResults;

    public UniversalSearchFragment() { }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_workout_search, container, false);
        firestoreService = new FirestoreService();

        SearchBar search = fragmentView.findViewById(R.id.search_bar);
        SearchView searchView  = fragmentView.findViewById(R.id.search_view);
        search.inflateMenu(R.menu.search_menu);
        search.setHint("Search...");

        // set up workout recycler view
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {});
        WorkoutClickListener clickListener = new WorkoutClickListener(displayWorkouts, activityLauncher);

        workoutRV = searchView.findViewById(R.id.search_rv);
        workoutRV.setHasFixedSize(true);
        workoutRV.setLayoutManager(new LinearLayoutManager(searchView.getContext()));
        workoutRV.setAdapter(new WorkoutRecAdapter(displayWorkouts, clickListener, true));


        Menu menu = search.getMenu();
        menu.setGroupCheckable(R.id.searchMenuSearchGroups, true, true);

        // Hackish, but this is literally a library in alpha.
        for (int i = 0; i < search.getMenu().size(); i++) {
            MenuItem item = search.getMenu().getItem(i);
            SpannableString coloredTitle = new SpannableString(item.getTitle());
            coloredTitle.setSpan(new ForegroundColorSpan(R.color.md_theme_light_onBackground), 0, coloredTitle.length(), 0);
            item.setTitle(coloredTitle);
        }

        AtomicBoolean includeWorkouts = new AtomicBoolean(false);
        AtomicBoolean includeGroups = new AtomicBoolean(false);
        AtomicBoolean includeUsers = new AtomicBoolean(false);

        search.setOnMenuItemClickListener(
                menuItem -> {
                    if (menuItem.isCheckable()) {
                        menuItem.setChecked(!menuItem.isChecked());

                        switch (menuItem.getItemId()) {
                            case R.id.search_menu_workout:
                                includeWorkouts.set(menuItem.isChecked());
                                break;
                            case R.id.search_menu_groups:
                                includeGroups.set(menuItem.isChecked());
                                break;
                            case R.id.search_menu_users:
                                includeUsers.set(menuItem.isChecked());
                                break;
                            default:
                                return false;
                        }
                    }

                    if (includeWorkouts.get()) {
                        menu.setGroupVisible(R.id.searchMenuWorkoutDifficultySortGroup, true);
                    } else {
                        menu.setGroupVisible(R.id.searchMenuWorkoutDifficultySortGroup, false);
                    }

                    return true;
                });

        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Trigger a search based on the category we selected.
                if (includeWorkouts.get()) {
                    firestoreService.findWorkoutsByCriteria(s.toString(), null, -1, -1,
                            new FindWorkoutsCallback(workoutCache, displayWorkouts, workoutRV), 10);
                } else if (includeGroups.get()) {

                } else if (includeUsers.get()) {

                } else {
                    // Invalid state
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**

        //noResults = searchView.findViewById(R.id.txt_no_workout_results);

        // populate categories dropdown
        //categoriesDropdown = searchView.findViewById(R.id.dropdown_categories);
        List<String> workoutCategories = WorkoutCategory.listCategories(true, true);
        workoutCategories.add(0, "Any");
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item,
                workoutCategories);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesDropdown.setAdapter(categoriesAdapter);
        categoriesDropdown.setSelection(0);
        prevFilter = null;
        categoriesDropdown.setOnItemSelectedListener(new CategoriesFilterListener());

        // populate sort dropdown
        sortOptions = new String[]{"Name ↑", "Name ↓", "Difficulty ↑", "Difficulty ↓"};
        //sortDropdown = searchView.findViewById(R.id.dropdown_workout_sort);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDropdown.setAdapter(sortAdapter);
        sortDropdown.setSelection(0);
        prevSort = sortOptions[0];
        sortDropdown.setOnItemSelectedListener(new SortListener());

        // add query listener to search view
        //SearchView workoutSearch = searchView.findViewById(R.id.sv_workout);
        //workoutSearch.setOnQueryTextListener(new WorkoutQueryListener());

        // set up workout recycler view
        //workoutRV = searchView.findViewById(R.id.rv_workout);
        workoutRV.setHasFixedSize(true);
        workoutRV.setLayoutManager(new LinearLayoutManager(searchView.getContext()));
        workoutRV.setAdapter(new WorkoutAdapter(displayWorkouts, container, searchView));
         */

        return fragmentView;
    }
}
