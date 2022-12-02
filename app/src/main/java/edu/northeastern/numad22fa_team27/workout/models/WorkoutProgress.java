package edu.northeastern.numad22fa_team27.workout.models;

public class WorkoutProgress {
    private Workout workout;
    private int timesCompleted;
    private int completionPercentage;

    public WorkoutProgress(Workout workout, int timesCompleted, int completionPercentage) {
        this.workout = workout;
        this.timesCompleted = timesCompleted;
        this.completionPercentage = completionPercentage;
    }

    public Workout getWorkout() {
        return workout;
    }

    public int getTimesCompleted() {
        return timesCompleted;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }
}
