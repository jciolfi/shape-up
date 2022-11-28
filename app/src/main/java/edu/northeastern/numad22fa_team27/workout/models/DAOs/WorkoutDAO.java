package edu.northeastern.numad22fa_team27.workout.models.DAOs;

import java.util.List;

import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;

public class WorkoutDAO {
    public String workoutName;
    public List<MediaParagraph> workoutDescription;
    public List<WorkoutCategory> categoriesPresent;
    public float difficulty;

    public WorkoutDAO() {}

    @Override
    public String toString() {
        return "WorkoutDAO{" +
                "workoutName='" + workoutName + '\'' +
                ", workoutDescription=" + workoutDescription +
                ", categoriesPresent=" + categoriesPresent +
                ", difficulty=" + difficulty +
                '}';
    }
}
