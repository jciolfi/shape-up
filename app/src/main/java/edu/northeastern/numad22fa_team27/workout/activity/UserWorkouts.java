package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.InterItemSpacer;
import edu.northeastern.numad22fa_team27.workout.adapters.UserWorkoutAdapter;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutProgress;
import edu.northeastern.numad22fa_team27.workout.test_utilities.FakeWorkoutGenerator;

public class UserWorkouts extends AppCompatActivity {
    private final static String TAG = "UserWorkouts";
    private final List<WorkoutProgress> originalWorkoutData = new ArrayList<>();
    private List<WorkoutProgress> displayedWorkoutData = new ArrayList<>();

    private void genFakeWorkoutCards() throws IOException {
        String json = Resources.toString(Resources.getResource("workout_links.json"), Charsets.UTF_8);
        FakeWorkoutGenerator gen = new FakeWorkoutGenerator();
        gen.loadAttributes(json);

        for (int i = 0; i <= 100; i++) {
            originalWorkoutData.add(new WorkoutProgress(gen.newRandomWorkout(), i, i));
        }
    }

    private List<WorkoutProgress> searchWorkouts(String targetString) {
        String searchString = targetString.toLowerCase();
        return originalWorkoutData.stream().filter(k -> k.getWorkout().getWorkoutName().toLowerCase().contains(searchString)).collect(Collectors.toList());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_workouts);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        RecyclerView lists = findViewById(R.id.userWorkoutRecView);
        lists.setHasFixedSize(true);
        lists.setAdapter(new UserWorkoutAdapter(displayedWorkoutData));
        lists.setLayoutManager(manager);

        lists.addItemDecoration(new InterItemSpacer());

        // Create dummy data
        try {
            genFakeWorkoutCards();
        } catch (IOException e) {
            Log.e(TAG, "Could not load data!");
            return;
        }
        displayedWorkoutData.addAll(originalWorkoutData);

        // Load data
        Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();

        EditText userWorkoutSearch = findViewById(R.id.userWorkoutSearch);
        userWorkoutSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                displayedWorkoutData.clear();
                displayedWorkoutData.addAll(searchWorkouts(s.toString()));
                Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();
            }
        });
    }
}