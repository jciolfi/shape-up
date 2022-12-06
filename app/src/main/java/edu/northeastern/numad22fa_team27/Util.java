package edu.northeastern.numad22fa_team27;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

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

    public static void requestNoActivityBar(AppCompatActivity origin) {
        origin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        origin.getSupportActionBar().hide();
        origin.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static boolean stringIsNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String limitLength(String s, int length) {
        if (length < 1) {
            return "";
        } else if (s.length() <= length) {
            return s;
        }

        return s.substring(0,length) + "…";
    }
}
