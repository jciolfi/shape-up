package edu.northeastern.numad22fa_team27;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnA7 = findViewById(R.id.btn_A7);
        btnA7.setOnClickListener(view -> Util.openActivity(this, SpotifyActivity.class));
    }
}