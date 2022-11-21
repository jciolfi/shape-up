package edu.northeastern.numad22fa_team27.workout.models;

// Categories taken from Crossfit's 2002 article "What Is Fitness?"
// at https://journal.crossfit.com/article/what-is-fitness
// and https://www.crossfit.com/essentials/what-is-fitness-lecture-10-physical-skills

import android.content.res.Resources;

import edu.northeastern.numad22fa_team27.R;

public enum WorkoutCategory {
    ENDURANCE, // The ability of the body’s systems to gather, process, and deliver oxygen
    STAMINA, // The ability of body systems to process, deliver, store, and utilize energy
    STRENGTH, // The ability of a muscular unit, or combination of muscular units, to apply force
    FLEXIBILITY, // The ability to maximize the range of motion at a given joint
    POWER, // The ability of a muscular unit, or combination of muscular units, to apply maximum force in minimum time
    SPEED, // The ability to minimize the cycle time of a repeated movement
    COORDINATION, // The ability to combine several distinct movement patterns into a singular distinct movement
    AGILITY, // The ability to minimize transition time from one movement pattern to another
    BALANCE, // The ability to control the placement of the body’s center of gravity in relation to its support base
    ACCURACY; // The ability to control movement in a given direction or at a given intensity

    public static String describe(WorkoutCategory w) {
        switch (w) {
            case ENDURANCE:
                return Resources.getSystem().getString(R.string.workout_endurance_description);
            case STAMINA:
                return Resources.getSystem().getString(R.string.workout_stamina_description);
            case STRENGTH:
                return Resources.getSystem().getString(R.string.workout_strength_description);
            case FLEXIBILITY:
                return Resources.getSystem().getString(R.string.workout_flexibility_description);
            case POWER:
                return Resources.getSystem().getString(R.string.workout_power_description);
            case SPEED:
                return Resources.getSystem().getString(R.string.workout_speed_description);
            case COORDINATION:
                return Resources.getSystem().getString(R.string.workout_coordination_description);
            case AGILITY:
                return Resources.getSystem().getString(R.string.workout_agility_description);
            case BALANCE:
                return Resources.getSystem().getString(R.string.workout_balance_description);
            case ACCURACY:
                return Resources.getSystem().getString(R.string.workout_accuracy_description);
            default:
                break;
        }
        return "???";
    }
}
