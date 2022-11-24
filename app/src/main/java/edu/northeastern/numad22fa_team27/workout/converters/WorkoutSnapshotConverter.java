package edu.northeastern.numad22fa_team27.workout.converters;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.Workout;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class WorkoutSnapshotConverter implements ModelConverter<DataSnapshot, Workout>{
    private static String TAG = "WorkoutSnapshotConverter";

    public DataSnapshot pack(Workout obj) {
        return null;
    }

    public Workout unpack(DataSnapshot obj) {
        Workout builtWorkout = new Workout();

        for (DataSnapshot elem : obj.getChildren()) {
            if (elem.getKey().equals("categoriesPresent")) {
                builtWorkout.setCategoriesPresent(StreamSupport.stream(elem.getChildren().spliterator(), false)
                        .map(e -> WorkoutCategory.valueOf(e.getValue(String.class)))
                        .collect(Collectors.toList()));
            } else if (elem.getKey().equals("difficulty")) {
                builtWorkout.setDifficulty(((Number) elem.getValue()).floatValue());
            } else if (elem.getKey().equals("workoutDescription")) {
                builtWorkout.setWorkoutDescription(StreamSupport.stream(elem.getChildren().spliterator(), false)
                        .map(e -> new MediaParagraph(
                                e.child("paragraphText").getValue(String.class),
                                e.child("mediaURL").getValue(String.class)))
                        .collect(Collectors.toList()));
            } else if (elem.getKey().equals("workoutName")) {
                builtWorkout.setWorkoutName(elem.getValue(String.class));
            } else if (elem.getKey().equals("workoutID")) {
                builtWorkout.setWorkoutID(new UUID(
                        elem.child("mostSignificantBits").getValue(Long.class),
                        elem.child("leastSignificantBits").getValue(Long.class)));
            } else {
                Log.e(TAG, String.format("Received unknown workout attribute \"%s\"!", elem.getKey()));
            }
        }

        return builtWorkout;
    }
}
