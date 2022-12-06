package edu.northeastern.numad22fa_team27.workout.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.activity.WorkoutDisplay;
import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class WorkoutClickListener {
    private final ActivityResultLauncher<Intent> launcher;
    List<Workout> cardData;

    public WorkoutClickListener(List<Workout> originalData, ActivityResultLauncher<Intent> activityLauncher) {
        this.launcher = activityLauncher;
        this.cardData = originalData;
    }


    public void onClick(View v, int index) {
        Workout w = cardData.get(index);

        Activity source = (Activity) v.getContext();
        Intent intent = new Intent(source, WorkoutDisplay.class);
        intent.putParcelableArrayListExtra("Text", (ArrayList<MediaParagraph>) w.getWorkoutDescription());
        intent.putExtra("WorkoutId", w.getWorkoutID());
        intent.putExtra("Difficulty", w.getDifficulty());
        intent.putExtra("Title", w.getWorkoutName());
        intent.putExtra("Categories", String.join(", ", w.getCategoriesPresent().stream().map(c -> c.name()).collect(Collectors.toList())));
        launcher.launch(intent);
    }
}
