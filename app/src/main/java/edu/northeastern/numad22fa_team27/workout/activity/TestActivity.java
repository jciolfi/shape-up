package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class TestActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);

        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                String providedUsername = groupName.getText().toString();
                if (Util.stringIsNullOrEmpty(providedUsername)) {
                    groupName.setError("Username can't be empty");
                } else {
                    // TODO: create group with UUID?

                    groupDialog.dismiss();
                }
            });
        });
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
