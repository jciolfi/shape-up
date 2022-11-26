package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindWorkoutCallback;
import edu.northeastern.numad22fa_team27.workout.models.UserDAO;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindGroupsCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindUserGroupsCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindUsersCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.WorkoutCallback;
import edu.northeastern.numad22fa_team27.workout.models.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutDAO;

/**
 * TODO
 * - use constants for "workouts, groups, users"
 * - abstract callback classes into singletons?
 * - pagination where necessary
 */

public class TestActivity extends AppCompatActivity {
    private UserDAO.User user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String testUserID = "d8532ef1-07b2-44f4-ba12-576ad8ced33a";
        getUser(testUserID);

        Button createGroup = findViewById(R.id.btn_create_group);
        createGroup.setOnClickListener(view ->
                createGroup("test group 2", UUID.fromString(testUserID)));

        Button findGroups = findViewById(R.id.btn_find_groups);
        findGroups.setOnClickListener(view ->
                findGroupsByName("test", new FindGroupsCallback()));

        Button findUserGroups = findViewById(R.id.btn_find_user_groups);
        findUserGroups.setOnClickListener(view ->
                findUserGroups(testUserID, new FindUserGroupsCallback()));

        Button findUsers = findViewById(R.id.btn_find_users);
        findUsers.setOnClickListener(view ->
                findUserByUsername("test", new FindUsersCallback()));

        Button findWorkouts = findViewById(R.id.btn_find_workouts);
        findWorkouts.setOnClickListener(view ->
                findWorkoutsByCriteria("test", null, new FindWorkoutCallback()));
    }

    private void getUser(String userID) {
        mDatabase.child("users")
                .child(userID)
                .get().addOnSuccessListener(snapshot -> {
                    user = snapshot.getValue(UserDAO.User.class);
                    if (user != null) {
                        Log.d("GETUSER", user.toString());
                    } else {
                        Log.e("GETUSER", "user was null");
                    }
                });
    }

    private void createGroup(String groupName, UUID creatorID) {
        // insert group into DB
        GroupDAO newGroup = new GroupDAO(groupName, creatorID);
        mDatabase.child("groups")
                .child(String.valueOf(newGroup.getGroupID()))
                .setValue(newGroup.pack())
                .addOnSuccessListener(unused1 -> {

                    // associate new group with creator
                    if (this.user.getJoinedGroups() == null) {
                        this.user.setJoinedGroups(new ArrayList<>());
                    }

                    this.user.addGroup(newGroup.getGroupID());
                    mDatabase.child("users")
                            .child(String.valueOf(creatorID))
                            .child("joinedGroups")
                            .setValue(this.user.getJoinedGroups()).addOnSuccessListener(unused2 -> {
                                Toast.makeText(
                                        this,
                                        String.format("Successfully created group %s!", groupName),
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                });
    }

    private void findWorkoutsByCriteria(String workoutName, WorkoutCategory workoutCategory, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(workoutName) && workoutCategory == null) {
            warnBadParam("findWorkoutsByCriteria");
        } else if (Util.stringIsNullOrEmpty(workoutName)) {
            // search by ONLY workout category
            // https://stackoverflow.com/questions/40656589/firebase-query-if-child-of-child-contains-a-value

        } else {
            // Firebase can't do compound queries, Firestore can :(
            // search by workout name (and filter by workout category)
            mDatabase.child("workouts")
                    .orderByChild("workoutName")
                    .startAt(workoutName)
                    .endAt(workoutName + '\uf8ff')
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<WorkoutDAO> workouts = new ArrayList<>();
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                WorkoutDAO workout = ds.getValue(WorkoutDAO.class);
                                if (workout != null && workout.containsCategory(workoutCategory)) {
                                    workouts.add(workout);
                                }
                            }
                            callback.processWorkout(workouts);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void findUserByUsername(String username, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(username)) {
            warnBadParam("findUserByUsername");
            return;
        }

        mDatabase.child("users")
                .orderByChild("username")
                .startAt(username)
                .endAt(username + '\uf8ff')
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.process(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void findUserGroups(String userID, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(userID)) {
            warnBadParam("findUserGroups");
            return;
        }

        mDatabase.child("users")
                .child(userID)
                .child("joinedGroups")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.process(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void findGroupsByName(String groupName, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(groupName)) {
            warnBadParam("findGroupsByCriteria");
            return;
        }

        mDatabase.child("groups")
                .orderByChild("name")
                .startAt(groupName)
                .endAt(groupName + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.process(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void warnBadParam(String methodName) {
        Log.w("Test", String.format("%s: fields were null", methodName));
    }
}
