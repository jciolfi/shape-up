package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.callbacks.CreateGroupCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindUserGroupsCallback;
import edu.northeastern.numad22fa_team27.workout.fragments.UniversalSearchFragment;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.models.universal_search.NavigationBar;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UserGroupsActivity extends AppCompatActivity {
    private final static String TAG = "LeaderboardActivity";
    private FirestoreService firestoreService;
    private RecyclerView userGroupsRV;
    private final List<Group> userGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_groups);

        firestoreService = new FirestoreService();

        // Set up nav bar
        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setSelectedItemId(R.id.nav_groups);
        bottomNav.setOnItemSelectedListener(NavigationBar.setNavListener(this));
        FloatingActionButton fabSearch = findViewById(R.id.addGroupButton);
        fabSearch.setOnClickListener(v -> createGroup());
        // End setup nav bar

        // set up recycler view
        userGroupsRV = findViewById(R.id.rv_user_groups);
        userGroupsRV.setLayoutManager(new LinearLayoutManager(this));
        firestoreService.findUserGroups(new FindUserGroupsCallback(userGroups, userGroupsRV));
    }

    private void createGroup() {
        final EditText groupNameText = new EditText(this);

        AlertDialog createGroupDialog = new AlertDialog.Builder(this)
                .setTitle("Create Group")
                .setMessage("Enter a name for you group")
                .setView(groupNameText)
                .setPositiveButton("Create", (dialogInterface, i) -> {
                    if (!Util.stringIsNullOrEmpty(groupNameText.getText().toString())) {
                        firestoreService.tryCreateGroup(groupNameText.getText().toString(),
                                new CreateGroupCallback(userGroups, userGroupsRV));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        createGroupDialog.show();
    }
}
