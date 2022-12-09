package edu.northeastern.numad22fa_team27;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM reg", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("FCM reg", token);
//                        Toast.makeText(MainActivity.this, "Your device registeration token is: " + token, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}