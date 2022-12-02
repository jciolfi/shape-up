package edu.northeastern.numad22fa_team27.workout.models.workout_search;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class WorkoutViewHolder extends RecyclerView.ViewHolder {
    public TextView workoutName;
    private final View itemView;

    public WorkoutViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.workoutName = itemView.findViewById(R.id.workout_item);
    }
}
