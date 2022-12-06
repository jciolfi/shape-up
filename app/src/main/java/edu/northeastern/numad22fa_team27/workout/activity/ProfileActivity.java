package edu.northeastern.numad22fa_team27.workout.activity;

import static edu.northeastern.numad22fa_team27.Util.requestNoActivityBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecCard;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.NavigationBar;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class ProfileActivity extends AppCompatActivity {
    private FirestoreService firestoreService;

    private FirebaseAuth user_auth;
    private Button settingsBtn, friendsBtn, completedWorkoutsBtn;
    private ImageView profilePic;
    private RecyclerView recWorkouts, friendWorkouts;

    private class ImageGetterThread implements Runnable {
        private final int height;
        private final int width;
        private CountDownLatch latch = new CountDownLatch(1);
        private List<Bitmap> bitmaps = new ArrayList<>();
        private String[] given_urls;

        public ImageGetterThread(String[] urls, int width, int height) {
            this.given_urls = urls;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            for (String url : given_urls){
                try {
                    bitmaps.add(Picasso.get()
                            .load(url)
                            .resize(width, height)
                            .centerCrop(Gravity.CENTER)
                            .get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            latch.countDown();
        }

        public List<Bitmap> collect() {
            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bitmaps;
        }
    }

    public List<WorkoutRecCard> genFakeWorkouts(int width, int height) {
        String[] urls = {
                "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
                "https://plus.unsplash.com/premium_photo-1665673312770-90df9f77ddfa?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8N3x8Z3ltfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
                "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                "https://images.unsplash.com/photo-1576678927484-cc907957088c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80"
        };

        ImageGetterThread it = new ImageGetterThread(urls, width, height);
        Thread th = new Thread(it);
        th.start();
        List<Bitmap> images = it.collect();
        List<WorkoutRecCard> workouts = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            workouts.add(new WorkoutRecCard(images.get(i), String.format("Epic Workout %d", i), "afdas asdasdas gsdhewhad"));
        }
        return workouts;
    }

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

        List<WorkoutRecCard> recWorkoutCards = genFakeWorkouts(1024, 512);
        List<WorkoutRecCard> friendWorkoutCards = genFakeWorkouts(512, 512);

        // Set any callbacks
        friendsBtn.setOnClickListener(v -> Util.openActivity(ProfileActivity.this, MyFriendsActivity.class));
        settingsBtn.setOnClickListener(v -> Util.openActivity(ProfileActivity.this, SettingsActivity.class));
        completedWorkoutsBtn.setOnClickListener(v -> Util.openActivity(ProfileActivity.this, UserWorkouts.class));

        // Set any adapters
        setupRecView(recWorkouts, recWorkoutCards, false);
        setupRecView(friendWorkouts, friendWorkoutCards, true);

        firestoreService = new FirestoreService();
    }

    private void setupRecView(RecyclerView rv, List<WorkoutRecCard> dataset, boolean isVertical) {
        int orientation = isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL;
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager manager = new LinearLayoutManager(this, orientation, false);
        rv.setHasFixedSize(true);
        rv.setAdapter(new WorkoutRecAdapter(dataset, isVertical));
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