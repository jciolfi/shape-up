package edu.northeastern.numad22fa_team27;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import edu.northeastern.numad22fa_team27.spotify.SpotifyActivity;
import edu.northeastern.numad22fa_team27.sticker_messenger.FirebaseActivity;

import edu.northeastern.numad22fa_team27.workout.activity.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnA7 = findViewById(R.id.btn_A7);
        btnA7.setOnClickListener(view -> Util.openActivity(this, SpotifyActivity.class));
        Button btnA8 = findViewById(R.id.btn_A8);
        btnA8.setOnClickListener(view -> Util.openActivity(this, FirebaseActivity.class));
        Button btnSU = findViewById(R.id.btn_proj);
        btnSU.setOnClickListener(view -> Util.openActivity(this, LoginActivity.class));
    }
}