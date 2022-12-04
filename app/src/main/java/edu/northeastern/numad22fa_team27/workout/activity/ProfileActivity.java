package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class ProfileActivity extends AppCompatActivity {
    private FirestoreService firestoreService;

    private TextView usr_email;
    private FirebaseAuth user_auth;
    private Button signOutBtn, settingsBtn, friendsBtn, completedWorkoutsBtn;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firestoreService = new FirestoreService();

        usr_email = findViewById(R.id.pusername);
        user_auth = FirebaseAuth.getInstance();
        signOutBtn = findViewById(R.id.signOutBtn);
        profilePic = findViewById(R.id.profilePic);

        friendsBtn = findViewById(R.id.tmpMyFriends);
        settingsBtn = findViewById(R.id.tmpSettings);
        completedWorkoutsBtn = findViewById(R.id.tmpCompletedWorkouts);

        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openActivity(ProfileActivity.this, MyFriendsActivity.class);
            }
        });

        signOutBtn.setOnClickListener(view -> {
            user_auth.signOut();
            Toast.makeText(ProfileActivity.this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
            Util.openActivity(ProfileActivity.this, LoginActivity.class);
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openActivity(ProfileActivity.this, SettingsActivity.class);
            }
        });

        completedWorkoutsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openActivity(ProfileActivity.this, UserWorkouts.class);
            }
        });


        loadUser();
        //settingsBtnClicked();
    }

    private void loadUser() {
        usr_email.setText(user_auth.getCurrentUser().getEmail());
    }

    private void groupsBtnClicked() {
        //firestoreService.findUserGroups(new FindGroupsCallback());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("users").document(currentID);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String url = task.getResult().getString("profilePic");
                    String username = task.getResult().getString("username");
                    usr_email.setText(username);
                    if (!url.isEmpty()) {
                        Picasso.get()
                                .load(url)
                                .resize(100, 100)
                                .into(profilePic);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Couldn't fetch the profile for the user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private NavigationBarView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_leaderboard:
                            Intent intent = new Intent(ProfileActivity.this, WorkoutListActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_profile:
                            intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_workout:
                            intent = new Intent(ProfileActivity.this, WorkoutListActivity.class);
                            startActivity(intent);
                            break;


                    }
                    return false;
                }
            };
}