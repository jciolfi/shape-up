package edu.northeastern.numad22fa_team27.workout.callbacks;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutSearch.WorkoutItem;

public class FindWorkoutsCallback extends WorkoutCallback {
    private final List<WorkoutItem> workouts;
    private final RecyclerView workoutRV;

    public FindWorkoutsCallback(List<WorkoutItem> workouts, RecyclerView workoutRV) {
        this.workouts = workouts;
        this.workoutRV = workoutRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        workouts.clear();
        for (WorkoutDAO workout : snapshot.toObjects(WorkoutDAO.class)) {
            workouts.add(new WorkoutItem(workout.workoutName));
        }
        Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
    }
}
