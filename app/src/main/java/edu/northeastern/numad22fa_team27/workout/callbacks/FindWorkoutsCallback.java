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
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class FindWorkoutsCallback extends WorkoutCallback {
    private final List<WorkoutDAO> queryWorkouts;
    private final List<WorkoutDAO> displayWorkouts;
    private final WorkoutCategory category;
    private final RecyclerView workoutRV;
    private final TextView noResults;

    public FindWorkoutsCallback(List<WorkoutDAO> queryWorkouts,
                                List<WorkoutDAO> displayWorkouts,
                                WorkoutCategory category,
                                RecyclerView workoutRV,
                                TextView noResults) {
        this.queryWorkouts = queryWorkouts;
        this.displayWorkouts = displayWorkouts;
        this.category = category;
        this.workoutRV = workoutRV;
        this.noResults = noResults;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
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

        // display message when no results returned
        if (displayWorkouts.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.INVISIBLE);
        }

        Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
    }
}
