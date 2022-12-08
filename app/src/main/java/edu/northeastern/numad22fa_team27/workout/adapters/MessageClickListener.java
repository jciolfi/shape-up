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
import edu.northeastern.numad22fa_team27.workout.models.Message;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class MessageClickListener {
    private final ActivityResultLauncher<Intent> launcher;
    List<Message> cardData;

    public MessageClickListener(List<Message> originalData, ActivityResultLauncher<Intent> activityLauncher) {
        this.launcher = activityLauncher;
        this.cardData = originalData;
    }


    public void onClick(View v, int index) {
        Message m = cardData.get(index);

        Activity source = (Activity) v.getContext();
        //fix the intent so that it goes to a message activity
        //Intent intent = new Intent(source, WorkoutDisplay.class);
        /*intent.putParcelableArrayListExtra("Text", (ArrayList<MediaParagraph>) m.getWorkoutDescription());
        intent.putExtra("WorkoutId", m.getWorkoutID());
        intent.putExtra("Difficulty", m.getDifficulty());
        intent.putExtra("Title", m.getWorkoutName());
        intent.putExtra("Categories", String.join(", ", m.getCategoriesPresent().stream().map(c -> c.name()).collect(Collectors.toList())));
        launcher.launch(intent);*/
    }
}
