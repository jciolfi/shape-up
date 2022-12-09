package edu.northeastern.numad22fa_team27.workout.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.workout.models.DAO.WorkoutDAO;

/**
 * Object representation of a single workout exercise
 */
public class Workout {
    private String workoutID;
    private String workoutName;
    private List<MediaParagraph> workoutDescription;
    private List<WorkoutCategory> categoriesPresent;
    private float difficulty;

    private String coverURL;
    private String blurb;

    public Workout() {
        this.workoutID = UUID.randomUUID().toString();
        this.workoutName = new String();
        this.workoutDescription = new ArrayList<>();
        this.categoriesPresent = new ArrayList<>();
        this.difficulty = -1.0f;
        this.coverURL = new String();
        this.blurb = new String();
    }

    public Workout(String workoutUUID, String workoutName, List<MediaParagraph> workoutDescription, List<WorkoutCategory> categoriesPresent, float difficulty, String coverURL, String blurb) {
        this.workoutID = workoutUUID;
        this.workoutName = workoutName;
        this.workoutDescription = workoutDescription;
        this.categoriesPresent = categoriesPresent;
        this.difficulty = difficulty;
        this.coverURL = coverURL;
        this.blurb = blurb;
    }

    public Workout(WorkoutDAO wd) {
        this.workoutID = wd.workoutID;
        this.workoutName = wd.workoutName;
        this.workoutDescription = wd.workoutDescription;
        this.categoriesPresent = wd.categoriesPresent;
        this.difficulty = wd.difficulty;
        this.coverURL = wd.coverURL;
        this.blurb = wd.blurb;
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

    public String getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(String uuid) {
        workoutID = uuid;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "workoutID='" + workoutID + '\'' +
                ", workoutName='" + workoutName + '\'' +
                ", workoutDescription=" + workoutDescription +
                ", categoriesPresent=" + categoriesPresent +
                ", difficulty=" + difficulty +
                '}';
    }


}
