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
    public ImageView workoutImage;
    public TextView numBadges;

    public UserWorkoutHolder(View view) {
        super(view);
        views(view);
    }

    private void views(View view) {
        workoutName = view.findViewById(R.id.userFriendName);
        categoryName = view.findViewById(R.id.userWorkoutCardCategory);
        workoutImage = view.findViewById(R.id.workoutCover);
        numBadges = view.findViewById(R.id.userWorkoutBadges);
    }
}