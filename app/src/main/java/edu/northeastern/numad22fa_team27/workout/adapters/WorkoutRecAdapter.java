package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import edu.northeastern.numad22fa_team27.R;

public class WorkoutRecAdapter extends RecyclerView.Adapter<WorkoutRecHolder> {

    private final List<WorkoutRecCard> list;
    private final int layout;

    public WorkoutRecAdapter(List<WorkoutRecCard> list, boolean isVertical) {
        this.list = list;
        this.layout = (isVertical) ? R.layout.workout_card_horizontal : R.layout.workout_card_vertical;
    }

    @NonNull
    @Override
    public WorkoutRecHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WorkoutRecHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutRecHolder h, int i) {
        WorkoutRecCard card = list.get(i);
        h.title.setText(card.getTitle());
        h.blurb.setText(card.getBlurb());
        h.workoutPicture.setImageBitmap(card.getWorkoutImage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}