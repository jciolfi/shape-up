package edu.northeastern.numad22fa_team27.workout.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserTest {
    private List<Workout> workouts = new ArrayList<>();

    @Before
    public void setUp() {
        // Quickly generate some placeholder data
        MediaParagraph mp1 = new MediaParagraph("Text", "Link");
        MediaParagraph mp2 = new MediaParagraph("Text", "Link");

        workouts.add(new Workout(UUID.randomUUID().toString(), "w1", List.of(mp1), List.of(WorkoutCategory.BALANCE), 5.0F));
        workouts.add(new Workout(UUID.randomUUID().toString(), "w2", List.of(mp1, mp2), List.of(WorkoutCategory.BALANCE, WorkoutCategory.ACCURACY), 4.5F));
        workouts.add(new Workout(UUID.randomUUID().toString(), "w3", List.of(mp2), List.of(WorkoutCategory.STRENGTH), 2.0F));
        workouts.add(new Workout(UUID.randomUUID().toString(), "w4", List.of(mp2, mp1), List.of(WorkoutCategory.POWER, WorkoutCategory.SPEED, WorkoutCategory.FLEXIBILITY), 3.99F));
    }

    @Test
    public void testRecordWorkout() {
        LocalDate today = LocalDate.now();
        User user = new User("User", "abc", "Image");

        // D - 4 : Balance x1
        user.recordWorkout(workouts.get(0), today.minusDays(4));

        // D - 3 : Balance x1, Accuracy x1
        user.recordWorkout(workouts.get(1), today.minusDays(3));

        // D - 2 : Balance x1, Strength x1
        user.recordWorkout(workouts.get(2), today.minusDays(2));
        user.recordWorkout(workouts.get(0), today.minusDays(2));

        // D - 1 : Balance x2, Accuracy x1, Strength x1
        user.recordWorkout(workouts.get(3), today.minusDays(1));
        user.recordWorkout(workouts.get(1), today.minusDays(1));
        user.recordWorkout(workouts.get(0), today.minusDays(1));

        assertEquals(4, user.getCurrentStreak(WorkoutCategory.BALANCE));
        assertEquals(4, user.getBestStreak(WorkoutCategory.BALANCE));

        // Miss a good 48 hours between workouts. NOTE: Updates are driven on-demand, so we need to
        // explicitly record missed workouts for each day in the real app
        user.recordWorkout(workouts.get(0), today.plusDays(1));

        assertEquals(1, user.getCurrentStreak(WorkoutCategory.BALANCE));
        assertEquals(4, user.getBestStreak(WorkoutCategory.BALANCE));


    }
}