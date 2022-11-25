package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class TestActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button createGroup = findViewById(R.id.btn_create_group);
        createGroup.setOnClickListener(view -> createGroup());

        Button findGroups = findViewById(R.id.btn_find_groups);
        findGroups.setOnClickListener(view -> findGroupsByCriteria("test"));

        Button findUserGroups = findViewById(R.id.btn_find_user_groups);
        findUserGroups.setOnClickListener(view -> findUserGroups("testuser1"));

        Button findUsers = findViewById(R.id.btn_find_users);
        findUsers.setOnClickListener(view -> findUserByUsername("test"));

        Button findWorkouts = findViewById(R.id.btn_find_workouts);
        findWorkouts.setOnClickListener(view -> findWorkoutsByCriteria("test", null));
    }

    private void createGroup() {
        final EditText groupName = new EditText(this);
        groupName.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog groupDialog = new AlertDialog.Builder(this)
                .setTitle("Create New Group")
                .setMessage("Enter the name for your group")
                .setView(groupName)
                .setPositiveButton("Create", null)
                .setCancelable(false)
                .create();

        groupDialog.setOnShowListener(dialogInterface -> {
            Button createButton = groupDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            createButton.setOnClickListener(view -> {
                String providedGroupName = groupName.getText().toString();
                if (Util.stringIsNullOrEmpty(providedGroupName)) {
                    groupName.setError("Group name can't be empty");
                } else {
                    GroupDAO newGroup = new GroupDAO(providedGroupName, "SOME USER");
                    mDatabase
                            .child("group")
                            .child(String.valueOf(newGroup.getGroupID()))
                            .setValue(newGroup.pack())
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(
                                        this,
                                        String.format("Successfully created group %s!", providedGroupName),
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                    groupDialog.dismiss();
                }
            });
        });

        groupDialog.show();
    }

    // TODO: think about pagination (.startAt/.startAfter/.limit)
    private void findWorkoutsByCriteria(String workoutName, WorkoutCategory workoutCategory) {
        if (Util.stringIsNullOrEmpty(workoutName) && workoutCategory == null) {
            warnBadParam("findWorkoutsByCriteria");
        } else if (Util.stringIsNullOrEmpty(workoutName)) {

        } else if (workoutCategory == null) {

        } else {

        }
    }

    private void findUserByUsername(String username) {
        if (Util.stringIsNullOrEmpty(username)) {
            warnBadParam("findUserByUsername");
            return;
        }

        mDatabase.child("users")
                .startAt(username)
                .endAt(username + '\uf8ff')
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

    }

    private void findUserGroups(String username) {
        if (Util.stringIsNullOrEmpty(username)) {
            warnBadParam("findUserGroups");
            return;
        }

        mDatabase.child("users")
                .child("joinedGroups")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void findGroupsByCriteria(String groupName) {
        if (Util.stringIsNullOrEmpty(groupName)) {
            warnBadParam("findGroupsByCriteria");
            return;
        }

        // TODO: same substring method as find users by username
    }

    private void warnBadParam(String methodName) {
        Log.w("Test", String.format("%s: fields were null", methodName));
    }
}
