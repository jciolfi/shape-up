package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAOs.WorkoutDAO;

public class MigrationActivity extends AppCompatActivity {
    private final String TAG = "MigrationActivity";
    private DatabaseReference firebaseDB;
    private FirebaseFirestore firestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migration);
        firestoreDB = FirebaseFirestore.getInstance();
        firebaseDB = FirebaseDatabase.getInstance().getReference();

        Button users = findViewById(R.id.btn_MIGRATE_USERS);
        users.setOnClickListener(view -> migrateUsers());

        Button groups = findViewById(R.id.btn_MIGRATE_GROUPS);

        Button workouts = findViewById(R.id.btn_MIGRATE_WORKOUTS);
        workouts.setOnClickListener(view -> migrateWorkouts());
    }

    private void migrateWorkouts() {
        firebaseDB.child("workouts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            WorkoutDAO w = ds.getValue(WorkoutDAO.class);
                            if (w != null && ds.getKey() != null) {
                                firestoreDB.collection("workouts")
                                        .document(ds.getKey())
                                        .set(w);
                            } else {
                                Log.w(TAG, String.format("%s WAS NULL!", ds.getKey()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void migrateUsers() {

    }
}
