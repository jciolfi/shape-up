package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class ProfileActivity extends AppCompatActivity {
    private FirestoreService firestoreService;


    private TextView usr_email;
    private FirebaseAuth user_auth;
    private Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firestoreService = new FirestoreService();

        usr_email = findViewById(R.id.pusername);
        user_auth = FirebaseAuth.getInstance();
        signOutBtn = findViewById(R.id.signOutBtn);
        Button groupsButton = findViewById(R.id.myGroups);
        groupsButton.setOnClickListener(view -> groupsBtnClicked());

        loadUser();
        setSignOutBtnClicked();
    }

    public void loadUser() {
        usr_email.setText(user_auth.getCurrentUser().getEmail());
    }

    public void setSignOutBtnClicked() {
        signOutBtn.setOnClickListener(view -> {
            user_auth.signOut();
            Toast.makeText(ProfileActivity.this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
            Util.openActivity(ProfileActivity.this, LoginActivity.class);
        });
    }

    private void groupsBtnClicked() {
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
                    firestoreService.createGroup(providedGroupName);
                    groupDialog.dismiss();
                }
            });
        });

        groupDialog.show();
    }


    // TODO
    // Implement the logic when user presses back button.
}