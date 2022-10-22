package edu.northeastern.numad22fa_team27;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class with common helper functions.
 */
public class Util {
    /**
     * Open a target activity from an originating activity
     * @param origin the activity navigating away from
     * @param target the activity to navigate to
     */
    public static void openActivity(Activity origin, Class<?> target) {
        Intent intent = new Intent(origin, target);
        origin.startActivity(intent);
    }
}
