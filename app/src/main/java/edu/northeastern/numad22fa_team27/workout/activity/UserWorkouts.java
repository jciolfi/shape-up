package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.InterItemSpacer;
import edu.northeastern.numad22fa_team27.workout.adapters.UserWorkoutAdapter;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutProgress;
import edu.northeastern.numad22fa_team27.workout.utilities.UserUtil;

public class UserWorkouts extends AppCompatActivity {
    private final static String TAG = "UserWorkouts";
    private RecyclerView workouts;
    private final List<WorkoutProgress> originalWorkoutData = new ArrayList<>();
    private final List<WorkoutProgress> displayedWorkoutData = new ArrayList<>();

    private void genFakeWorkoutCards() throws IOException, InterruptedException {
        Map<String, Integer> completedWorkouts = UserUtil.getInstance().getUser().getWorkoutCompletions();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Map.Entry<String, Integer> kv : completedWorkouts.entrySet()) {
            db.collection(Constants.WORKOUTS)
                    .document(kv.getKey())
                    .get().addOnSuccessListener(ds -> {
                        Workout w = new Workout(ds.toObject(WorkoutDAO.class));
                        synchronized (originalWorkoutData) {
                            WorkoutProgress newWorkout = (new WorkoutProgress(w, completedWorkouts.get(w.getWorkoutID())));
                            originalWorkoutData.add(newWorkout);
                            displayedWorkoutData.add(newWorkout);
                            Objects.requireNonNull(workouts.getAdapter()).notifyItemInserted(displayedWorkoutData.size() - 1);
                        }
                    });
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
        workouts = findViewById(R.id.userWorkoutRecView);
        workouts.setHasFixedSize(true);
        workouts.setAdapter(new UserWorkoutAdapter(displayedWorkoutData));
        workouts.setLayoutManager(manager);
        workouts.addItemDecoration(new InterItemSpacer(12));

        // Create dummy data
        try {
            genFakeWorkoutCards();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Could not load data!");
            return;
        }

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
                Objects.requireNonNull(workouts.getAdapter()).notifyDataSetChanged();
            }
        });

        displayedWorkoutData.addAll(originalWorkoutData);

        // Load data
        Objects.requireNonNull(workouts.getAdapter()).notifyDataSetChanged();
    }
}