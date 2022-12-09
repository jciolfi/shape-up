package edu.northeastern.numad22fa_team27.workout.activity;

import static edu.northeastern.numad22fa_team27.Util.requestNoActivityBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutClickListener;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecAdapter;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.NavigationBar;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;
import edu.northeastern.numad22fa_team27.workout.services.RecommendationService;
import edu.northeastern.numad22fa_team27.workout.utilities.UserUtil;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private FirebaseAuth user_auth;
    private Button settingsBtn, friendsBtn, completedWorkoutsBtn;
    private ImageView profilePic;
    private RecyclerView recWorkouts, friendWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNoActivityBar(this);
        setContentView(R.layout.activity_profile);

        // Set up nav bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_toolbar);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(NavigationBar.setNavListener(this));

        // Find all our UI elements
        user_auth = FirebaseAuth.getInstance();
        profilePic = findViewById(R.id.profilePic);
        completedWorkoutsBtn = findViewById(R.id.chip_workouts);
        friendsBtn = findViewById(R.id.chip_friends);
        settingsBtn = findViewById(R.id.chip_settings);
        recWorkouts = findViewById(R.id.rec_workouts);
        friendWorkouts = findViewById(R.id.friend_workouts);

        // Set any callbacks
        friendsBtn.setOnClickListener(v -> Util.openActivity(ProfileActivity.this, MyFriendsActivity.class));
        settingsBtn.setOnClickListener(v -> Util.openActivity(ProfileActivity.this, SettingsActivity.class));
        completedWorkoutsBtn.setOnClickListener(v -> Util.openActivity(ProfileActivity.this, UserWorkouts.class));

        // Storage for workout recommendations in recyclerview
        List<Workout> recWorkoutCards = new ArrayList<>();
        List<Workout> friendWorkoutCards = new ArrayList<>();

        // Set any adapters
        setupRecView(recWorkouts, recWorkoutCards, false);
        setupRecView(friendWorkouts, friendWorkoutCards, true);

        // TODO: Better recommendations
        RecommendationService rs = new RecommendationService(null);
        rs.RecommendWorkouts(recWorkouts, recWorkoutCards);
        rs.RecommendFriendApprovedWorkouts(friendWorkouts, friendWorkoutCards);
    }

    private void setupRecView(RecyclerView rv, List<Workout> dataset, boolean isVertical) {
        // Part of the pain and suffering required by Google to have an onClick method for a
        // Recyclerview without using deprecated methods
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        String workoutId = extras.getString("WorkoutId");
                        Boolean completedWorkout = extras.getBoolean("Success");
                        if (completedWorkout) {
                            Toast.makeText(this, String.format("Congrats on completing workout %s", workoutId), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Okay, maybe next time.", Toast.LENGTH_LONG).show();
                        }

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection(Constants.WORKOUTS).document(workoutId).get().addOnSuccessListener(
                            ds -> {
                                Workout w = new Workout(ds.toObject(WorkoutDAO.class));
                                User self = UserUtil.getInstance().getUser();
                                self.recordWorkout(w, LocalDate.now());
                                db.collection(Constants.USERS)
                                        .document(user_auth.getUid())
                                        .set(new UserDAO(self))
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, e.getMessage());
                                        });
                            }
                        ).addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });

                        // TODO: Update user
                    }
                });

        WorkoutClickListener clickListener = new WorkoutClickListener(dataset, activityLauncher);

        int orientation = isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL;
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager manager = new LinearLayoutManager(this, orientation, false);
        rv.setHasFixedSize(true);
        rv.setAdapter(new WorkoutRecAdapter(dataset, clickListener, isVertical));
        rv.setLayoutManager(manager);

        Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("users").document(currentID);
        reference.get().addOnCompleteListener(task -> {
            if(task.getResult().exists()) {
                String url = task.getResult().getString("profilePic");
                if (!url.isEmpty()) {
                    Picasso.get()
                            .load(url)
                            .resize(100, 100)
                            .into(profilePic);
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Couldn't fetch the profile for the user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}