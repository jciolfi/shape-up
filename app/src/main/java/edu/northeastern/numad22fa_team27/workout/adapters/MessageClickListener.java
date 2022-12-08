package edu.northeastern.numad22fa_team27.workout.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.activity.ReadMessageActivity;
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
        //look at the workout click listener to see where it goes and how the intent gets the information
        Intent intent = new Intent(source, ReadMessageActivity.class);

        intent.putExtra("chatId", m.getChatId());
        intent.putExtra("title", m.getName());

        launcher.launch(intent);
    }
}
