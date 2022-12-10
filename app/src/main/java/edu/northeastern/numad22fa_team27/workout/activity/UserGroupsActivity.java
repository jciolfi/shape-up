package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.callbacks.CreateGroupCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindUserGroupsCallback;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.models.user_groups.UserGroupsAdapter;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.NavigationBar;
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

        // set up listener for creating group
        Button createGroupBtn = findViewById(R.id.btn_create_group);
        createGroupBtn.setOnClickListener(view -> createGroup());

        // set up nav bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_toolbar);
        bottomNav.setSelectedItemId(R.id.nav_groups);
        bottomNav.setOnItemSelectedListener(NavigationBar.setNavListener(this));

        // set up recycler view
        userGroupsRV = findViewById(R.id.rv_user_groups);
        userGroupsRV.setLayoutManager(new LinearLayoutManager(this));
        // this will set adapter.
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
