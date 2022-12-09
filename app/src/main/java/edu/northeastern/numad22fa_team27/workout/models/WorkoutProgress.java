package edu.northeastern.numad22fa_team27.workout.models;

public class WorkoutProgress {
    private Workout workout;
    private int timesCompleted;

    public WorkoutProgress(Workout workout, int timesCompleted) {
        this.workout = workout;
        this.timesCompleted = timesCompleted;
    }

    public Workout getWorkout() {
        return workout;
    }

    public int getTimesCompleted() {
        return timesCompleted;
    }

    public String getCoverURL() {
        return workout.getCoverURL();
    }
}
