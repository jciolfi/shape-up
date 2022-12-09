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
    public WorkoutClickListener listener;

    public WorkoutRecHolder(View view, WorkoutClickListener listener) {
        super(view);

        this.listener = listener;
        this.workoutPicture = view.findViewById(R.id.result_icon);
        this.title = view.findViewById(R.id.search_item_title);
        this.blurb  = view.findViewById(R.id.search_item_misc);

        view.findViewById(R.id.card_base).setOnClickListener(view1 -> {
            listener.onClick(view1, getLayoutPosition());
        });
    }
}
