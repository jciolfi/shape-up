package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutProgress;

public class WorkoutStepAdapter extends RecyclerView.Adapter<WorkoutStepHolder> {

    private final List<MediaParagraph> list;

    public WorkoutStepAdapter(List<MediaParagraph> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public WorkoutStepHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WorkoutStepHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_steps_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutStepHolder holder, int position) {
        MediaParagraph card = list.get(position);
        holder.steps.setText(card.getParagraphText());
        Picasso.get().load(card.getMediaURL()).into(holder.workoutPicture);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}