package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UserSearchActivity  extends AppCompatActivity {
    private final String TAG = "UserSearchActivity";
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        firestoreService = new FirestoreService();

        // set up user recycler view

    }
}
