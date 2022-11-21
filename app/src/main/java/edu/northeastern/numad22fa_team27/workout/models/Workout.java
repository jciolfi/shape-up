package edu.northeastern.numad22fa_team27.workout.models;

import java.util.List;
import java.util.UUID;

/**
 * Object representation of a single workout exercise
 */
public class Workout {
    private UUID workoutID;
    private String workoutName;
    private List<MediaParagraph> workoutDescription;
    private List<WorkoutCategory> categoriesPresent;
    private float difficulty;

    public Workout(UUID workoutID, String workoutName, List<MediaParagraph> workoutDescription, List<WorkoutCategory> categoriesPresent, float difficulty) {
        this.workoutID = workoutID;
        this.workoutName = workoutName;
        this.workoutDescription = workoutDescription;
        this.categoriesPresent = categoriesPresent;
        this.difficulty = difficulty;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    /**
     * @param workoutName Name of workout exercise
     * @return True on setter success, false on failure
     */
    public boolean setWorkoutName(String workoutName) {
        if (workoutName.isEmpty()) {
            return false;
        }
        this.workoutName = workoutName;
        return true;
    }

    public float getDifficulty() {
        return difficulty;
    }

    /**
     * @param difficulty Difficulty of exercise, on a scale from 0-5
     * @return True on setter success, false on failure
     */
    public boolean setDifficulty(float difficulty) {
        if (difficulty < 0 || difficulty > 5) {
            return false;
        }
        this.difficulty = difficulty;
        return true;
    }

    public List<WorkoutCategory> getCategoriesPresent() {
        return categoriesPresent;
    }

    /**
     * @param categoriesPresent Applicable workout categories that describe this exercise
     * @return True on setter success, false on failure
     */
    public boolean setCategoriesPresent(List<WorkoutCategory> categoriesPresent) {
        if (categoriesPresent.isEmpty()) {
            return false;
        }
        this.categoriesPresent = categoriesPresent;
        return true;
    }

    public List<MediaParagraph> getWorkoutDescription() {
        return workoutDescription;
    }

    /**
     * @param workoutDescription Collection of text-media paragraphs describing this exercise
     * @return True on setter success, false on failure
     */
    public boolean setWorkoutDescription(List<MediaParagraph> workoutDescription) {
        if (workoutDescription.isEmpty()) {
            return false;
        }
        this.workoutDescription = workoutDescription;
        return true;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "workoutName='" + workoutName + '\'' +
                ", categoriesPresent=" + categoriesPresent +
                ", difficulty=" + difficulty +
                '}';
    }

    public UUID getWorkoutID() {
        return workoutID;
    }
}
