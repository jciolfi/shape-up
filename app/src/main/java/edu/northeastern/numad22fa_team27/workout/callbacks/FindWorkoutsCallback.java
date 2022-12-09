package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class FindWorkoutsCallback extends WorkoutCallback {
    private final List<Workout> queryWorkouts;
    private final List<Workout> displayWorkouts;
    private final RecyclerView workoutRV;

    public FindWorkoutsCallback(List<Workout> queryWorkouts,
                                List<Workout> displayWorkouts,
                                RecyclerView workoutRV) {
        this.queryWorkouts = queryWorkouts;
        this.displayWorkouts = displayWorkouts;
        this.workoutRV = workoutRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        // add all query workouts
        queryWorkouts.clear();
        List<WorkoutDAO> intermediary = snapshot.toObjects(WorkoutDAO.class);
        queryWorkouts.addAll(intermediary.stream()
                .map(wd -> new Workout(wd))
                .collect(Collectors.toList()));

        // add workouts that contain the given category
        // TODO: Filtering from a list
        displayWorkouts.clear();
        displayWorkouts.addAll(queryWorkouts);

        Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
    }
}
