package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class FriendProfileActivity extends AppCompatActivity {

    private TextView friend_email;
    private ImageView friend_profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        friend_email = findViewById(R.id.friendUsername);
        friend_profilePic = findViewById(R.id.friendProfilePic);

        extractInformation();
    }

    private void extractInformation() {
        String username = getIntent().getStringExtra("USERNAME");
        String url = getIntent().getStringExtra("PROFILEPIC");
        friend_email.setText(username);
        Picasso.get()
                .load(url)
                .resize(120, 120)
                .into(friend_profilePic);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String currentID = user.getUid();
//        DocumentReference reference;
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//        reference = firestore.collection("users").document(currentID);
//        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.getResult().exists()) {
//                    String url = task.getResult().getString("profilePic");
//                    String username = task.getResult().getString("username");
//                    usr_email.setText(username);
//                    if (!url.isEmpty()) {
//                        Picasso.get()
//                                .load(url)
//                                .resize(100, 100)
//                                .into(profilePic);
//                    }
//                } else {
//                    Toast.makeText(ProfileActivity.this, "Couldn't fetch the profile for the user", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
}