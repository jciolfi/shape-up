package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import android.graphics.drawable.Icon;

public class WorkoutCard {

    //private final Icon workoutImage;
    private final int imgInt;
    private final String title;
    private final Boolean isComplete;

    public WorkoutCard(int imgInt, String title, boolean isComplete) {
        //this.workoutImage = workoutImage;
        this.imgInt = imgInt;
        this.title = title;
        this.isComplete = isComplete;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public int getImgInt() {//Icon getWorkoutImage() {
        return imgInt; //workoutImage;
    }

}
