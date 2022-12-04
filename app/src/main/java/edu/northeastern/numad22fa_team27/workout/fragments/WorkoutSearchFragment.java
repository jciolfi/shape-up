package edu.northeastern.numad22fa_team27.workout.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindWorkoutsCallback;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.WorkoutAdapter;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class WorkoutSearchFragment extends Fragment {
    private final String TAG = "WorkoutSearchActivity";
    private FirestoreService firestoreService;
    private Spinner categoriesDropdown;
    private Spinner sortDropdown;
    private RecyclerView workoutRV;
    private String[] sortOptions;
    // workouts returned from search view
    private final List<WorkoutDAO> workoutCache = new ArrayList<>();
    // workouts filtered on category
    private final List<WorkoutDAO> displayWorkouts = new ArrayList<>();
    private WorkoutCategory prevFilter;
    private String prevSort;
    private TextView noResults;

    public WorkoutSearchFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_workout_search, container, false);

        firestoreService = new FirestoreService();
        noResults = searchView.findViewById(R.id.txt_no_workout_results);

        // populate categories dropdown
        categoriesDropdown = searchView.findViewById(R.id.dropdown_categories);
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
        sortDropdown = searchView.findViewById(R.id.dropdown_workout_sort);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDropdown.setAdapter(sortAdapter);
        sortDropdown.setSelection(0);
        prevSort = sortOptions[0];
        sortDropdown.setOnItemSelectedListener(new SortListener());

        // add query listener to search view
        SearchView workoutSearch = searchView.findViewById(R.id.sv_workout);
        workoutSearch.setOnQueryTextListener(new WorkoutQueryListener());

        // set up workout recycler view
        workoutRV = searchView.findViewById(R.id.rv_workout);
        workoutRV.setHasFixedSize(true);
        workoutRV.setLayoutManager(new LinearLayoutManager(searchView.getContext()));
        workoutRV.setAdapter(new WorkoutAdapter(displayWorkouts, container, searchView));

        return searchView;
    }

    private class WorkoutQueryListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // get selected category
            WorkoutCategory selectedCategory = WorkoutCategory.toCategory(
                    (String)categoriesDropdown.getSelectedItem());
            prevFilter = selectedCategory;

            // update displayWorkouts and workoutCache
            firestoreService.findWorkoutsByCriteria(query, null,
                    new FindWorkoutsCallback(workoutCache, displayWorkouts, selectedCategory, workoutRV, noResults));

            // reset sort
            sortDropdown.setSelection(0);
            prevSort = sortOptions[0];

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

    private class CategoriesFilterListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // get selected category
            WorkoutCategory selectedCategory = WorkoutCategory.toCategory(
                    (String)categoriesDropdown.getSelectedItem());

            // don't do extra work if we don't need to (select same filter again)
            if (selectedCategory == prevFilter) {
                return;
            }
            prevFilter = selectedCategory;

            // filter workouts on selected category
            displayWorkouts.clear();
            if (selectedCategory == null) {
                displayWorkouts.addAll(workoutCache);
            } else {
                displayWorkouts.addAll(workoutCache.stream()
                        .filter(w -> w.categoriesPresent.contains(selectedCategory))
                        .collect(Collectors.toList()));
            }

            // display message when no results returned
            if (displayWorkouts.size() == 0) {
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.INVISIBLE);
            }

            // notify workouts changed
            Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    }

    private class SortListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // don't do extra work if we don't need to (select same sort again)
            if (prevSort.equals(sortOptions[position])) {
                return;
            }
            prevSort = sortOptions[position];
            boolean shouldNotify = true;

            // sort by selected option
            // return <0 if w1 comes before w2, >0 if w2 comes before w1, =0 if tie
            switch (position) {
                // Name ↑ (ascending a->z)
                case 0: {
                    displayWorkouts.sort(Comparator.comparing(w -> w.workoutName));
                    break;
                }
                // Name ↓ (descending: z-a)
                case 1: {
                    displayWorkouts.sort((w1, w2) -> -(w1.workoutName.compareTo(w2.workoutName)));
                    break;
                }
                // Difficulty ↑ (ascending: 1->10)
                case 2: {
                    displayWorkouts.sort((w1, w2) -> sortWorkouts(w1, w2, true));
                    break;
                }
                // Difficulty ↓ (descending: 10->1)
                case 3: {
                    displayWorkouts.sort((w1, w2) -> sortWorkouts(w1, w2, false));
                    break;
                }
                default: {
                    shouldNotify = false;
                    break;
                }
            }

            if (shouldNotify) {
                Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }

        private int sortWorkouts(WorkoutDAO w1, WorkoutDAO w2, boolean ascending) {
            float diff = w1.difficulty - w2.difficulty;
            if (diff < 0) {
                return ascending ? -1 : 1;
            } else if (diff > 0) {
                return ascending ? 1 : -1;
            } else {
                return 0;
            }
        }
    }
}
