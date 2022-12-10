package edu.northeastern.numad22fa_team27.workout.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.activity.ProfileActivity;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutClickListener;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecAdapter;
import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class MessageService {

    private final User self;
    private final FirebaseFirestore firestoreDB;

    public MessageService(User self) {
        this.self = self;
        this.firestoreDB = FirebaseFirestore.getInstance();
    }

    // only need this for loading messages
    public void listMessages(RecyclerView rv, List<Workout> target) {
        // TODO: Algorithm that takes into account past workouts, and completed workout difficulty

        // Pretend we found the average
        Double userAvgDifficulty = 3.0;
        Double userMaxDifficulty = Math.max(userAvgDifficulty + 1, 5);
        Double userMinDifficulty = Math.max(userAvgDifficulty - 1, 0);


        //get information on the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        //[Ljava.lang.String

        //CollectionReference users = firestoreDB.collection("users");


        firestoreDB.collection("workouts")
                .whereGreaterThanOrEqualTo("difficulty", userMinDifficulty)
                .whereLessThan("difficulty", userMaxDifficulty)
                .limit(10)
                .get()
                .addOnSuccessListener(ds -> {
                    target.clear();

                    // Convert DAO object to underlying model
                    target.addAll(ds.toObjects(WorkoutDAO.class).stream()
                            .map(wd -> new Workout(wd.workoutID, wd.workoutName, wd.workoutDescription, wd.categoriesPresent, wd.difficulty, wd.coverURL, wd.blurb))
                            .collect(Collectors.toList()));

                    Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
                });
    }
}
