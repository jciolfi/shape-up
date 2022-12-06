package edu.northeastern.numad22fa_team27.workout.adapters;

import android.graphics.Bitmap;

public class WorkoutRecCard {

    private final Bitmap workoutImage;
    private final String title;
    private final String blurb;

    public WorkoutRecCard(Bitmap workoutImage, String title, String blurb) {
        this.workoutImage = workoutImage;
        this.title = title;
        this.blurb = blurb;
    }

    public String getTitle() {
        return title;
    }

    public String getBlurb() {
        return blurb;
    }

    public Bitmap getWorkoutImage() {//Icon getWorkoutImage() {
        return workoutImage;
    }

}
