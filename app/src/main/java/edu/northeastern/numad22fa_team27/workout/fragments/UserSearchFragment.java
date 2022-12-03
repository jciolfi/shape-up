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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindUsersCallback;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.user_search.UserAdapter;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UserSearchFragment extends Fragment {
    private final String TAG = "UserSearchFragment";
    private FirestoreService firestoreService;
    private Spinner sortDropdown;
    private String[] sortOptions;
    private String prevSort;
    private RecyclerView userRV;
    private final List<UserDAO> displayUsers = new ArrayList<>();
    private TextView noResults;

    public UserSearchFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_user_search, container, false);

        firestoreService = new FirestoreService();
        noResults = searchView.findViewById(R.id.txt_no_user_results);

        // populate sort dropdown
        sortOptions = new String[]{"Name ↑", "Name ↓"};
        sortDropdown = searchView.findViewById(R.id.dropdown_user_sort);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDropdown.setAdapter(sortAdapter);
        sortDropdown.setSelection(0);
        prevSort = sortOptions[0];
        sortDropdown.setOnItemSelectedListener(new SortListener());

        // add query listener to search view
        SearchView userSearch = searchView.findViewById(R.id.sv_users);
        userSearch.setOnQueryTextListener(new UserQueryListener());

        // set up user recycler view
        userRV = searchView.findViewById(R.id.rv_users);
        userRV.setHasFixedSize(true);
        userRV.setLayoutManager(new LinearLayoutManager(searchView.getContext()));
        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        userRV.setAdapter(new UserAdapter(displayUsers, container, searchView, userAuth.getCurrentUser()));

        return searchView;
    }

    private class UserQueryListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            firestoreService.findUsersByUsername(query, new FindUsersCallback(displayUsers, userRV, noResults));

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

            // sort by selected option
            // return <0 if w1 comes before w2, >0 if w2 comes before w1, =0 if tie
            switch (position) {
                // Name ↑ (ascending a->z)
                case 0: {
                    displayUsers.sort(Comparator.comparing(u -> u.username));
                    break;
                }
                // Name ↓ (descending: z-a)
                case 1: {
                    displayUsers.sort((u1, u2) -> -(u1.username.compareTo(u2.username)));
                    break;
                }
                default: {
                    shouldNotify = false;
                    break;
                }
            }

            if (shouldNotify) {
                Objects.requireNonNull(userRV.getAdapter()).notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    }
}
