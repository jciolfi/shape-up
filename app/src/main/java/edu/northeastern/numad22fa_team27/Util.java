package edu.northeastern.numad22fa_team27;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
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

    @SuppressLint("ResourceAsColor")
    public static void setMenuItemColor(MenuItem item){
        SpannableString coloredTitle = new SpannableString(item.getTitle());
        coloredTitle.setSpan(new ForegroundColorSpan(R.color.md_theme_light_onBackground), 0, coloredTitle.length(), 0);
        item.setTitle(coloredTitle);
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
        if (length < 1 || s == null) {
            return "";
        }

        return s.length() <= length ? s : s.substring(0,length) + "…";
    }

    public static <T> T nullOrDefault(T val, T defaultVal) {
        return val != null ? val : defaultVal;
    }
}
