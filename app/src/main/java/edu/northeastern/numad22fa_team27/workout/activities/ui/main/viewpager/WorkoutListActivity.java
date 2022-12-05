package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.workout_search.NavigationBar;

public class WorkoutListActivity extends AppCompatActivity implements View.OnClickListener{

    private final List<WorkoutCard> workouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

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