package edu.northeastern.numad22fa_team27.workout.callbacks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class FindWorkoutsCallback extends WorkoutCallback {
    private final List<Summarizeable> displayWorkouts;
    private final RecyclerView dataRv;

    public FindWorkoutsCallback(List<Summarizeable> displayWorkouts,
                                RecyclerView rv) {
        this.displayWorkouts = displayWorkouts;
        this.dataRv = rv;
    }

    @Override
    public void processQuery(@NonNull QuerySnapshot snapshot) {
        // add all query workouts
        displayWorkouts.clear();
        displayWorkouts.addAll(snapshot.toObjects(WorkoutDAO.class).stream()
                .map(wd -> new Workout(wd))
                .collect(Collectors.toList()));

        Objects.requireNonNull(dataRv.getAdapter()).notifyDataSetChanged();
    }
}
