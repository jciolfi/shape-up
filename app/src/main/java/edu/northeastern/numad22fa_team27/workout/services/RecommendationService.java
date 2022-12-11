package edu.northeastern.numad22fa_team27.workout.services;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class RecommendationService {
    private final User self;
    private final FirebaseFirestore firestoreDB;

    public RecommendationService(User self) {
        this.self = self;
        this.firestoreDB = FirebaseFirestore.getInstance();
    }

    /**
     * Recommend workouts based on user behavior.
     * @return List of workouts to try
     */
    public void RecommendWorkouts(RecyclerView rv, List<Workout> target) {
        // TODO: Algorithm that takes into account past workouts

        // Pretend we found the average
        Double userAvgDifficulty = (Math.random() * 5);
        Double userMaxDifficulty = Math.max(userAvgDifficulty + 0.5, 5);
        Double userMinDifficulty = Math.max(userAvgDifficulty - 0.5, 0);
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

    /**
     * Recommend workouts based on the behavior of the user's friends.
     * @return List of workouts to try
     */
    public void RecommendFriendApprovedWorkouts(RecyclerView rv, List<Workout> target) {
        // TODO: Algorithm that reports workouts many friends have done that we haven't.

        // Pretend we found the average
        Double friendAvgDifficulty = (Math.random() * 5);
        Double friendMaxDifficulty = Math.max(friendAvgDifficulty + 1, 5);
        Double friendMinDifficulty = Math.max(friendAvgDifficulty - 1, 0);
        firestoreDB.collection("workouts")
                .whereGreaterThanOrEqualTo("difficulty", friendMinDifficulty)
                .whereLessThan("difficulty", friendMaxDifficulty)
                .limit(10)
                .get()
                .addOnSuccessListener(ds -> {
                    target.clear();

                    // Convert DAO object to underlying model
                    target.addAll(ds.toObjects(WorkoutDAO.class).stream()
                            .map(wd -> new Workout(wd))
                            .collect(Collectors.toList()));

                    Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
                });
    }

    private class ImageGetterThread implements Runnable {
        private final int height;
        private final int width;
        private CountDownLatch latch = new CountDownLatch(1);
        private List<Bitmap> bitmaps = new ArrayList<>();
        private List<String> given_urls;

        public ImageGetterThread(List<String> urls, int width, int height) {
            this.given_urls = urls;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            for (String url : given_urls){
                try {
                    bitmaps.add(Picasso.get()
                            .load(url)
                            .resize(width, height)
                            .centerCrop(Gravity.CENTER)
                            .get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            latch.countDown();
        }

        public List<Bitmap> collect() {
            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bitmaps;
        }
    }
}
