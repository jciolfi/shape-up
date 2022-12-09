package edu.northeastern.numad22fa_team27.workout.models.workout_search;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutViewHolder> {
    private final List<WorkoutDAO> displayedWorkouts;
    private final ViewGroup container;
    private final View searchView;

    public WorkoutAdapter(List<WorkoutDAO> displayedWorkouts, ViewGroup container, View searchView) {
        this.displayedWorkouts = displayedWorkouts;
        this.container = container;
        this.searchView = searchView;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkoutViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_workout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutDAO workout = displayedWorkouts.get(position);
        holder.workoutName.setText(workout.workoutName);
        holder.workoutName.setOnClickListener(view -> {
            // build custom popup
            final Dialog workoutInfoDialog = new Dialog(searchView.getContext());
            workoutInfoDialog.setContentView(LayoutInflater.from(searchView.getContext())
                    .inflate(R.layout.dialog_workout_item, container, false));

            // set title
            TextView workoutTitle = workoutInfoDialog.findViewById(R.id.title_workout_name);
            workoutTitle.setText(workout.workoutName);

            // set difficulty info
            TextView difficulty = workoutInfoDialog.findViewById(R.id.txt_difficulty_info);
            difficulty.setText(String.format("Difficulty: %s / 5.0", workout.difficulty));

            // set categories info
            TextView categories = workoutInfoDialog.findViewById(R.id.txt_categories_info);
            String[] categoriesList = workout.categoriesPresent.stream()
                    .map(WorkoutCategory::formatString)
                    .toArray(String[]::new);
            String categoryIdentifier = categoriesList.length > 1 ? "Categories" : "Category";
            categories.setText(String.format("%s: %s",
                    categoryIdentifier,
                    String.join(", ", categoriesList)));

            // set up close button / dismiss listener
            Button closeButton = workoutInfoDialog.findViewById(R.id.btn_close_workout);
            workoutInfoDialog.setOnDismissListener(dialogInterface -> {
                // focus will go to search view and bring up keyboard - disable this
                //final View workoutView = searchView.findViewById(R.id.rv_workout);
                //workoutView.requestFocus();
            });
            closeButton.setOnClickListener(view1 -> {
                workoutInfoDialog.dismiss();
            });

            // TODO: what does positive button do?

            workoutInfoDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return displayedWorkouts.size();
    }
}
