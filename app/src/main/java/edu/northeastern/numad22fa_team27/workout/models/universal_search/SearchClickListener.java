package edu.northeastern.numad22fa_team27.workout.models.universal_search;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.activity.FriendProfileActivity;
import edu.northeastern.numad22fa_team27.workout.activity.GroupDisplay;
import edu.northeastern.numad22fa_team27.workout.activity.WorkoutDisplay;
import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class SearchClickListener {
    private final ActivityResultLauncher<Intent> launcher;
    List<Summarizeable> cardData;
    Activity parentActivity;

    public SearchClickListener(Activity parentActivity, List<Summarizeable> originalData, ActivityResultLauncher<Intent> activityLauncher) {
        this.launcher = activityLauncher;
        this.cardData = originalData;
        this.parentActivity = parentActivity;
    }

    public void onClick(View v, int index) {
        Summarizeable card = cardData.get(index);

        if (card instanceof Workout) {
            Workout w = (Workout) card;
            Intent intent = new Intent(this.parentActivity, WorkoutDisplay.class);
            intent.putParcelableArrayListExtra("Text", (ArrayList<MediaParagraph>) w.getWorkoutDescription());
            intent.putExtra("WorkoutId", w.getWorkoutID());
            intent.putExtra("Difficulty", w.getDifficulty());
            intent.putExtra("Title", w.getWorkoutName());
            intent.putExtra("Categories", String.join(", ", w.getCategoriesPresent().stream().map(c -> c.name()).collect(Collectors.toList())));
            launcher.launch(intent);
        } else if (card instanceof User) {
            User u  = (User) card;
            Intent intent = new Intent(this.parentActivity, FriendProfileActivity.class);
            intent.putExtra("USERID", u.getUserID());
            intent.putExtra("USERNAME", u.getUsername());
            intent.putExtra("PROFILEPIC", u.getProfilePic());
            launcher.launch(intent);
        } else if (card instanceof Group) {
            Group g  = (Group) card;
            Intent intent = new Intent(this.parentActivity, GroupDisplay.class);
            intent.putExtra("GROUP_ID", g.getGroupID());
            intent.putExtra("GROUP_NAME", g.getGroupName());
            launcher.launch(intent);
        }
    }
}
