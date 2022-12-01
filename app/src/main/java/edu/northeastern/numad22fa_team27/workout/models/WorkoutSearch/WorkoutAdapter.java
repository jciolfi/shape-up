package edu.northeastern.numad22fa_team27.workout.models.WorkoutSearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutViewHolder> {
    private final List<WorkoutItem> workouts;

    public WorkoutAdapter(List<WorkoutItem> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkoutViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        // need to get the results somehow
        String workoutName = workouts.get(position).getWorkoutName();
        holder.workoutName.setText(workoutName);

    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }
}
