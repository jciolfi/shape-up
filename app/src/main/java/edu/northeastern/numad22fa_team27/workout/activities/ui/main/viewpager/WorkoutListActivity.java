package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.activity.ProfileActivity;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.NavigationBar;

public class WorkoutListActivity extends AppCompatActivity implements View.OnClickListener{

    private final List<WorkoutCard> workouts = new ArrayList<>();
    private FirebaseAuth user_auth;
    private List<String> title;
    private String workoutTitle = "FlexibilityWorkout34";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        user_auth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {

        }else {
            BottomSheetDialogFragment bsdf = (BottomSheetDialogFragment)
                    this.getSupportFragmentManager().findFragmentByTag("instructionsFragTAG");
            if (bsdf != null) {

            }
        }


        workouts.add(new WorkoutCard(R.drawable.vinyl_vectorportal, "test 1", false));
        workouts.add(new WorkoutCard(R.drawable.baseball_vectorportal, "test 2", false));
        workouts.add(new WorkoutCard(R.drawable.arcade_vectorportal, "test 2", false));

        ViewPager2 workoutViewPager = findViewById(R.id.vpg_workout);
        PagerWorkoutAdapter pwa = new PagerWorkoutAdapter(workouts);
        workoutViewPager.setAdapter(pwa);

                workoutViewPager.setOnClickListener(view -> {


                });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_toolbar);
        bottomNav.setSelectedItemId(R.id.nav_workout);
        bottomNav.setOnItemSelectedListener(NavigationBar.setNavListener(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("workouts").document(currentID);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    Object workouts = task.getResult().get("workoutDescription", Object.class);//getString("profilePic");
                    String username = task.getResult().getString("username");
                    //usr_email.setText(username);
                    if (true) {
                        /*Picasso.get()
                                .load(url)
                                .resize(100, 100)
                                .into(profilePic);*/
                    }
                } else {
                    //Toast.makeText(ProfileActivity.this, "Couldn't fetch the profile for the user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_instructions) {
            PagerWorkoutAdapter pwa = new PagerWorkoutAdapter(workouts);
            ViewPager2 workoutViewPager = findViewById(R.id.vpg_workout);
            workoutViewPager.setAdapter(pwa);
            InstructionsFragment sheetFragment = new InstructionsFragment();
            sheetFragment.show(getSupportFragmentManager(),"instructionsFragTAG");

            sheetFragment.setInstructions(pwa.getCurrentInstructions(workoutViewPager.getCurrentItem()));

        }
    }
    //public voidonClickInstructions
}