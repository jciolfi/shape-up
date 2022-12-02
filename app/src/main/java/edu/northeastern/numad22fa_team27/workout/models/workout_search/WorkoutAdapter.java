package edu.northeastern.numad22fa_team27.workout.models.workout_search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutViewHolder> {
    private final List<WorkoutDAO> displayedWorkouts;

    public WorkoutAdapter(List<WorkoutDAO> displayedWorkouts) {
        this.displayedWorkouts = displayedWorkouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkoutViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_workout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        String workoutName = displayedWorkouts.get(position).workoutName;
        holder.workoutName.setText(workoutName);
    }

    @Override
    public int getItemCount() {
        return displayedWorkouts.size();
    }
}
