package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class WorkoutRecHolder extends RecyclerView.ViewHolder {
    public ImageView workoutPicture;
    public TextView title;
    public TextView blurb;

    public WorkoutRecHolder(View view) {
        super(view);
        workoutPicture = view.findViewById(R.id.img_workout);
        title = view.findViewById(R.id.txt_workout_title);
        blurb  = view.findViewById(R.id.workout_blurb);
    }
}
