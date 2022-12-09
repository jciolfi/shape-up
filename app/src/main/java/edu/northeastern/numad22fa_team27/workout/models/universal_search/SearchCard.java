package edu.northeastern.numad22fa_team27.workout.models.universal_search;

import android.graphics.Bitmap;

public class SearchCard {

    private final Bitmap workoutImage;
    private final String title;
    private final String blurb;

    public SearchCard(Bitmap workoutImage, String title, String blurb) {
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
