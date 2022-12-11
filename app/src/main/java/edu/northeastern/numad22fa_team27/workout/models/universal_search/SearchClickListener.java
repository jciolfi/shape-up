package edu.northeastern.numad22fa_team27.workout.models.universal_search;

import static edu.northeastern.numad22fa_team27.Constants.GROUPS;
import static edu.northeastern.numad22fa_team27.Constants.USERS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.activity.FriendProfileActivity;
import edu.northeastern.numad22fa_team27.workout.activity.WorkoutDisplay;
import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
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
            String Message = String.format("Would you like to join the group %s?\nMembers: %d", g.getGroupName(), g.getMembers().size());
            AlertDialog.Builder joinGroupBuilder = new AlertDialog.Builder(parentActivity)
                    .setTitle("Join group " + g.getGroupName() + "?")
                    .setMessage(Message)
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // TODO: Ugly. Clean this up.
                        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
                        FirebaseAuth userAuth = FirebaseAuth.getInstance();
                        firestoreDB.collection(USERS)
                                .document(userAuth.getUid())
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    UserDAO self = snapshot.toObject(UserDAO.class);
                                    g.getMembers().add(userAuth.getUid());
                                    self.joinedGroups.add(g.getGroupID());

                                    // Store
                                    firestoreDB.collection(USERS)
                                            .document(userAuth.getUid())
                                            .set(self);
                                    firestoreDB.collection(GROUPS)
                                            .document(g.getGroupID())
                                            .set(new GroupDAO(g));
                                });
                    }).setNegativeButton(
                    "No", (dialog, id) -> dialog.cancel());

            joinGroupBuilder.create().show();
            //
        }
    }
}
