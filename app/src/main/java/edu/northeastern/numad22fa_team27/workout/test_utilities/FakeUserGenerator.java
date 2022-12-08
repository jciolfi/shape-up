package edu.northeastern.numad22fa_team27.workout.test_utilities;

import static edu.northeastern.numad22fa_team27.Constants.USERS;
import static edu.northeastern.numad22fa_team27.Constants.WORKOUTS;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class FakeUserGenerator {
    private static FirebaseFirestore mDatabase;
    private int usersGenerated = 0;

    public FakeUserGenerator() {
        mDatabase = FirebaseFirestore.getInstance();
    }

    private String genUsername() {
        usersGenerated++;
        return "test_user" + usersGenerated + "@email.com";
    }

    private String genProfileImage() {
        return "ImageHere";
    }

    public User genNewUser() {
        return new User(
                genUsername(),
                genProfileImage()
        );
    }

    public void joinGroups(User u) {
        // TODO
    }

    public void doRandomWorkouts(User u, int numWorkouts) {
        mDatabase.collection(WORKOUTS)
                .limit(numWorkouts)
                .get()
                .addOnSuccessListener(ds -> {
                    List<Workout> wk = ds.toObjects(WorkoutDAO.class).stream()
                            .map(wd -> new Workout(wd))
                            .collect(Collectors.toList());

                    for (Workout w : wk) {
                        u.recordWorkout(w, LocalDate.now());
                    }
                });
    }

}
