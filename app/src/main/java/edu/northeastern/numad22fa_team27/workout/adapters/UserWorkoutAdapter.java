package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutProgress;

public class UserWorkoutAdapter extends RecyclerView.Adapter<UserWorkoutHolder> {

    private final List<WorkoutProgress> list;

    public UserWorkoutAdapter(List<WorkoutProgress> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public UserWorkoutHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new UserWorkoutHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_workouts_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserWorkoutHolder h, int i) {
        WorkoutProgress cards = list.get(i);
        getInfo(h, cards);
    }

    private void getInfo(UserWorkoutHolder h, WorkoutProgress cards) {
        String categories = cards.getWorkout().getCategoriesPresent().stream()
                .map(v -> v.name())
                .collect(Collectors.joining(", "));

        h.workoutName.setText(cards.getWorkout().getWorkoutName());
        h.categoryName.setText(categories);
        h.workoutProgress.setMax(100);
        h.workoutProgress.setProgress(cards.getCompletionPercentage());
        h.workoutProgress.invalidate();

        h.workoutProgressText.setText(String.format("%d%%", cards.getCompletionPercentage()));
        h.numBadges.setText(String.format("x%d", cards.getTimesCompleted()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}