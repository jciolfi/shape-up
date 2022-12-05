package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import android.graphics.drawable.Icon;

public class WorkoutCard {

    //private final Icon workoutImage;
    private final int imgInt;
    private final String title;
    private Boolean isComplete;
    private String description;

    public WorkoutCard(int imgInt, String title, boolean isComplete) {
        //this.workoutImage = workoutImage;
        this.imgInt = imgInt;
        this.title = title;
        this.isComplete = isComplete;
        this.description = title + "this is a test description to see how far off the edge it will go"
                + "this is a test description to see how far off the edge it will go"
                + "this is a test description to see how far off the edge it will go"
                + "this is a test description to see how far off the edge it will go"
                + "this is a test description to see how far off the edge it will go"
                + "this is a test description to see how far off the edge it will go"
                + "this is a test description to see how far off the edge it will go";
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

    public void setIsComplete(){
        this.isComplete = !this.isComplete;
    }

    public String getDescription() {
        return description;
    }

}
