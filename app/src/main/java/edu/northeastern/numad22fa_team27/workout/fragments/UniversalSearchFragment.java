package edu.northeastern.numad22fa_team27.workout.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.activity.FriendProfileActivity;
import edu.northeastern.numad22fa_team27.workout.activity.MyFriendsActivity;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindGroupsCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindUsersCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindWorkoutsCallback;
import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.universal_search.SearchAdapter;
import edu.northeastern.numad22fa_team27.workout.models.universal_search.SearchClickListener;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UniversalSearchFragment extends DialogFragment {
    private final String TAG = "WorkoutSearchActivity";
    private FirestoreService firestoreService;
    private RecyclerView searchRV;

    // workouts returned from search view
    private final List<Summarizeable> displayedResults = new ArrayList<>();
    private TextView noResults;

    public UniversalSearchFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firestoreService = new FirestoreService();
        AtomicBoolean includeWorkouts = new AtomicBoolean(true);
        AtomicBoolean includeGroups = new AtomicBoolean(false);
        AtomicBoolean includeUsers = new AtomicBoolean(false);
        AtomicBoolean reverseSort = new AtomicBoolean(false);

        // Inflate out actual view
        View fragmentView = inflater.inflate(R.layout.fragment_universal_search, container, false);

        // Find our searchbar + searchview and set up its filtering menu
        SearchBar search = fragmentView.findViewById(R.id.search_bar);
        SearchView searchView  = fragmentView.findViewById(R.id.search_view);
        search.requestFocusFromTouch();
        search.performClick();
        search.inflateMenu(R.menu.search_menu);

        // Set up search result recycler view
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                });
        SearchClickListener clickListener = new SearchClickListener(getActivity(), displayedResults, activityLauncher);

        searchRV = searchView.findViewById(R.id.search_rv);
        searchRV.setHasFixedSize(true);
        searchRV.setLayoutManager(new LinearLayoutManager(searchView.getContext()));
        searchRV.setAdapter(new SearchAdapter(displayedResults, clickListener));

        // Add menu functionality
        Menu menu = search.getMenu();
        menu.findItem(R.id.search_menu_workout).setChecked(true);
        menu.setGroupCheckable(R.id.searchMenuSearchGroups, true, true);

        // Hackish, but this is literally a library in alpha.
        for (int i = 0; i < search.getMenu().size(); i++) {
            setMenuItemColor(search.getMenu().getItem(i));
        }

        search.setOnMenuItemClickListener(
                menuItem -> {
                    if (menuItem.isCheckable()) {
                        menuItem.setChecked(true);

                        // Reset
                        includeWorkouts.set(false);
                        includeGroups.set(false);
                        includeUsers.set(false);

                        switch (menuItem.getItemId()) {
                            case R.id.search_menu_workout:
                                includeWorkouts.set(true);
                                break;
                            case R.id.search_menu_groups:
                                includeGroups.set(true);
                                break;
                            case R.id.search_menu_users:
                                includeUsers.set(true);
                                break;
                            default:
                                return false;
                        }
                    } else {
                        // Handle search and sort options
                        switch (menuItem.getItemId()) {
                            case R.id.search_menu_sort_alphabetical:
                                reverseSort.set(!reverseSort.get());
                                if (reverseSort.get()) {
                                    menuItem.setTitle("Name ↓");
                                } else {
                                    menuItem.setTitle("Name ↑");
                                }
                                setMenuItemColor(menuItem);
                                break;
                            default:
                                break;
                        }

                    }

                    if (includeWorkouts.get()) {
                        menu.setGroupVisible(R.id.searchMenuWorkoutDifficultySortGroup, true);
                    } else {
                        menu.setGroupVisible(R.id.searchMenuWorkoutDifficultySortGroup, false);
                    }

                    return true;
                });

        // Add callbacks to perform search when the user enters text
        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Trigger a search based on the category we selected.
                if (includeWorkouts.get()) {
                    firestoreService.findWorkoutsByCriteria(s.toString(), null, -1, -1,
                            new FindWorkoutsCallback(displayedResults, searchRV), 10, reverseSort.get());
                } else if (includeGroups.get()) {
                    firestoreService.findGroupsByName(s.toString(), new FindGroupsCallback(displayedResults, searchRV), reverseSort.get());
                } else if (includeUsers.get()) {
                    firestoreService.findUsersByUsername(s.toString(), new FindUsersCallback(displayedResults, searchRV), reverseSort.get());
                } else {
                    // Invalid state
                    Log.v("XYZ", "Invalid state!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return fragmentView;
    }

    @SuppressLint("ResourceAsColor")
    private void setMenuItemColor(MenuItem item){
        SpannableString coloredTitle = new SpannableString(item.getTitle());
        coloredTitle.setSpan(new ForegroundColorSpan(R.color.md_theme_light_onBackground), 0, coloredTitle.length(), 0);
        item.setTitle(coloredTitle);
    }
}
