package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class FindWorkoutsCallback extends WorkoutCallback {
    private final List<WorkoutDAO> queryWorkouts;
    private final List<WorkoutDAO> displayWorkouts;
    private final WorkoutCategory category;
    private final RecyclerView workoutRV;

    public FindWorkoutsCallback(List<WorkoutDAO> queryWorkouts,
                                List<WorkoutDAO> displayWorkouts,
                                WorkoutCategory category,
                                RecyclerView workoutRV) {
        this.queryWorkouts = queryWorkouts;
        this.displayWorkouts = displayWorkouts;
        this.category = category;
        this.workoutRV = workoutRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        for (WorkoutDAO w : snapshot.toObjects(WorkoutDAO.class)) {
            Log.d("XXX", w.toString());
        }

        // add all query workouts
        queryWorkouts.clear();
        queryWorkouts.addAll(snapshot.toObjects(WorkoutDAO.class));

        // add workouts that contain the given category
        displayWorkouts.clear();
        if (category == null) {
            displayWorkouts.addAll(snapshot.toObjects(WorkoutDAO.class));
        } else {
            displayWorkouts.addAll(snapshot.toObjects(WorkoutDAO.class).stream()
                    .filter(w -> w.categoriesPresent.contains(category))
                    .collect(Collectors.toList()));
        }

        Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
    }
}
