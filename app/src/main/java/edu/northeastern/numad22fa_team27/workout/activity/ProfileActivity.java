package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindGroupsCallback;
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
        firestoreService.findUserGroups(new FindGroupsCallback());
    }


    // TODO
    // Implement the logic when user presses back button.
}