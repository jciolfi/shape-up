package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class WorkoutStepHolder extends RecyclerView.ViewHolder {

    public ImageView workoutPicture;
    public TextView steps;

    public WorkoutStepHolder(View view) {
        super(view);

        this.workoutPicture = view.findViewById(R.id.workout_step_image);
        this.steps = view.findViewById(R.id.workout_step_text);
    }

}
