package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import edu.northeastern.numad22fa_team27.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView usr_email;
    private FirebaseAuth user_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usr_email = findViewById(R.id.pusername);
        user_auth = FirebaseAuth.getInstance();

        loadUser();
    }

    public void loadUser() {
        usr_email.setText(user_auth.getCurrentUser().getEmail());
    }
}