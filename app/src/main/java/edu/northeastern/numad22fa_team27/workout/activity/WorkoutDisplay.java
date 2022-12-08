package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutRecAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.WorkoutStepAdapter;
import edu.northeastern.numad22fa_team27.workout.models.MediaParagraph;

public class WorkoutDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_display);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {

            String workoutId = extras.getString("WorkoutId");
            TextView title = findViewById(R.id.detail_workout_name);
            title.setText(extras.getString("Title"));

            TextView difficulty = findViewById(R.id.detail_workout_difficulty);
            difficulty.setText(String.format("%.2f / 5.0", extras.getFloat("Difficulty")));

            TextView category = findViewById(R.id.detail_workout_category);
            category.setText(extras.getString("Categories"));

            List<MediaParagraph> steps = extras.getParcelableArrayList("Text");
            RecyclerView rv = findViewById(R.id.workout_steps_recycler);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
            rv.setHasFixedSize(true);
            rv.setAdapter(new WorkoutStepAdapter(steps));
            rv.setLayoutManager(manager);

            Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();

            Button doneBtn = findViewById(R.id.detail_workout_done_btn);
            doneBtn.setOnClickListener(v -> {
                Intent data = new Intent();
                data.putExtra("WorkoutId", workoutId);
                data.putExtra("Success", true);
                setResult(Activity.RESULT_OK, data);
                finish();
            });

            Button cancelBtn = findViewById(R.id.detail_workout_cancel_btn);
            cancelBtn.setOnClickListener(v -> {
                Intent data = new Intent();
                data.putExtra("WorkoutId", workoutId);
                data.putExtra("Success", false);
                setResult(Activity.RESULT_OK, data);
                finish();
            });
        }
    }
}