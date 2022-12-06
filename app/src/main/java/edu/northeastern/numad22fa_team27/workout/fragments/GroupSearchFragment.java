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

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindGroupsCallback;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.models.groups_search.GroupAdapter;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class GroupSearchFragment extends Fragment {
    private final String TAG = "GroupSearchFragment";
    private FirestoreService firestoreService;
    private Spinner sortDropdown;
    private String[] sortOptions;
    private String prevSort;
    private RecyclerView groupRV;
    private final List<Group> displayGroups = new ArrayList<>();
    private TextView noResults;

    public GroupSearchFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_group_search, container, false);

        firestoreService = new FirestoreService();
        noResults = searchView.findViewById(R.id.txt_no_group_results);

        // populate sort dropdown
        sortOptions = new String[]{"Name ↑", "Name ↓", "Popularity ↑", "Popularity ↓"};
        sortDropdown = searchView.findViewById(R.id.dropdown_groups_sort);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDropdown.setAdapter(sortAdapter);
        sortDropdown.setSelection(0);
        prevSort = sortOptions[0];
        sortDropdown.setOnItemSelectedListener(new SortListener());

        // add query listener to search view
        SearchView groupSearch = searchView.findViewById(R.id.sv_groups);
        groupSearch.setOnQueryTextListener(new GroupQueryListener());

        // set up recycler view
        groupRV = searchView.findViewById(R.id.rv_groups);
        groupRV.setHasFixedSize(true);
        groupRV.setLayoutManager(new LinearLayoutManager(searchView.getContext()));
        groupRV.setAdapter(new GroupAdapter(displayGroups, container, searchView));

        return searchView;
    }

    private class GroupQueryListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // query for groups
            firestoreService.findGroupsByName(query, new FindGroupsCallback(displayGroups, groupRV, noResults));

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

    private class SortListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            // don't do extra work if we don't need to (select same sort again)
            if (prevSort.equals(sortOptions[position])) {
                return;
            }
            prevSort = sortOptions[position];
            boolean shouldNotify = true;

            switch (position) {
                // Name ↑ (ascending a->z)
                case 0: {
                    displayGroups.sort(Comparator.comparing(Group::getGroupName));
                    break;
                }
                // Name ↓ (descending: z-a)
                case 1: {
                    displayGroups.sort((g1, g2) -> -(g1.getGroupName().compareTo(g2.getGroupName())));
                    break;
                }
                // Popularity ↑ (ascending: few -> many members)
                case 2: {
                    displayGroups.sort(Comparator.comparingInt(g -> g.getMembers().size()));
                    break;
                }
                // Popularity ↓ (descending: many -> few members)
                case 3: {
                    displayGroups.sort(Comparator.comparingInt(g -> -g.getMembers().size()));
                    break;
                }
                default: {
                    shouldNotify = false;
                    break;
                }
            }

            if (shouldNotify) {
                Objects.requireNonNull(groupRV.getAdapter()).notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    }
}
