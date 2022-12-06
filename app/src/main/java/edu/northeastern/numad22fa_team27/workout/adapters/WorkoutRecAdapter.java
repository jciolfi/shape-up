package edu.northeastern.numad22fa_team27.workout.adapters;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class WorkoutRecAdapter extends RecyclerView.Adapter<WorkoutRecHolder> {

    private final List<Workout> list;
    private WorkoutClickListener listener;
    private final int layout;
    private final int width;
    private final int height;

    public WorkoutRecAdapter(List<Workout> list, WorkoutClickListener listener, boolean isVertical) {
        this.listener = listener;
        this.list = list;
        this.layout = (isVertical) ? R.layout.workout_card_horizontal : R.layout.workout_card_vertical;
        this.width = isVertical ? 512 : 1024;
        this.height = 512;
    }

    @NonNull
    @Override
    public WorkoutRecHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WorkoutRecHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.layout, viewGroup, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutRecHolder h, int i) {
        Workout card = list.get(i);

        h.title.setText(card.getWorkoutName());
        h.blurb.setText(card.getBlurb());

        Picasso.get()
                .load(card.getCoverURL())
                .placeholder(R.drawable.workout_icon)
                .error(R.drawable.workout_icon)
                .resize(width, height)
                .centerCrop(Gravity.CENTER)
                .noFade()
                .into(h.workoutPicture);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}