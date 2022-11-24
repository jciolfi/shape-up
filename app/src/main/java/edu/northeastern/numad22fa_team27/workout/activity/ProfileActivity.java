package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;

public class ProfileActivity extends AppCompatActivity {

    private TextView usr_email;
    private FirebaseAuth user_auth;
    private Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usr_email = findViewById(R.id.pusername);
        user_auth = FirebaseAuth.getInstance();
        signOutBtn = findViewById(R.id.signOutBtn);

        loadUser();
        setSignOutBtnClicked();
    }

    public void loadUser() {
        usr_email.setText(user_auth.getCurrentUser().getEmail());
    }

    public void setSignOutBtnClicked() {
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_auth.signOut();
                Toast.makeText(ProfileActivity.this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
                Util.openActivity(ProfileActivity.this, LoginActivity.class);
            }
        });
    }

    // TODO
    // Implement the logic when user presses back button.
}