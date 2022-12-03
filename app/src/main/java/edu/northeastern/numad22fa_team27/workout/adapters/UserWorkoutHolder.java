package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;


public class UserWorkoutHolder extends RecyclerView.ViewHolder {
    public TextView workoutName;
    public TextView categoryName;
    public ProgressBar workoutProgress;
    public TextView workoutProgressText;
    public TextView numBadges;

    public UserWorkoutHolder(View view) {
        super(view);
        views(view);
    }

    private void views(View view) {
        workoutName = view.findViewById(R.id.userWorkoutCardName);
        categoryName = view.findViewById(R.id.userWorkoutCardCategory);
        workoutProgress = view.findViewById(R.id.userWorkoutCardProgress);
        workoutProgressText = view.findViewById(R.id.userWorkoutCardProgressText);
        numBadges = view.findViewById(R.id.userWorkoutBadges);
    }
}