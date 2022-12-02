package edu.northeastern.numad22fa_team27.workout.callbacks;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;

public class FindWorkoutsCallback extends WorkoutCallback {
    private final List<WorkoutDAO> workouts;
    private final RecyclerView workoutRV;

    public FindWorkoutsCallback(List<WorkoutDAO> workouts, RecyclerView workoutRV) {
        this.workouts = workouts;
        this.workoutRV = workoutRV;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        workouts.clear();
        workouts.addAll(snapshot.toObjects(WorkoutDAO.class));
        Objects.requireNonNull(workoutRV.getAdapter()).notifyDataSetChanged();
    }
}
