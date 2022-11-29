package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.callbacks.FindGroupsCallback;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class ProfileActivity extends AppCompatActivity {
    private FirestoreService firestoreService;


    private TextView usr_email;
    private FirebaseAuth user_auth;
    private Button signOutBtn, settingsBtn;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firestoreService = new FirestoreService();

        usr_email = findViewById(R.id.pusername);
        user_auth = FirebaseAuth.getInstance();
        signOutBtn = findViewById(R.id.signOutBtn);
        settingsBtn = findViewById(R.id.mySettings);
        profilePic = findViewById(R.id.profilePic);

        loadUser();
        setSignOutBtnClicked();
        settingsBtnClicked();
    }

    public void loadUser() {
        usr_email.setText(user_auth.getCurrentUser().getEmail());
    }

    public void setSignOutBtnClicked() {
        signOutBtn.setOnClickListener(view -> {
            user_auth.signOut();
            Toast.makeText(ProfileActivity.this, "Successfully signed out!", Toast.LENGTH_SHORT).show();
            Util.openActivity(ProfileActivity.this, LoginActivity.class);
        });
    }


    public void settingsBtnClicked() {
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openActivity(ProfileActivity.this, SettingsActivity.class);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentID = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("users").document(currentID);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String url = task.getResult().getString("profilePic");

                    Picasso.get().load(url).into(profilePic);
                } else {
                    Toast.makeText(ProfileActivity.this, "Couldn't fetch the profile for the user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
// TODO
    // Implement the logic when user presses back button.
}