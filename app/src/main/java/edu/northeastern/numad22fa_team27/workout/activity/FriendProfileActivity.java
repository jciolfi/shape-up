package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;

public class FriendProfileActivity extends AppCompatActivity {

    private TextView friend_email;
    private ImageView friend_profilePic;
    private Button removeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        friend_email = findViewById(R.id.friendUsername);
        friend_profilePic = findViewById(R.id.friendProfilePic);
        removeBtn = findViewById(R.id.friendRemoveBtn);

        extractInformation();

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend();
                Util.openActivity(FriendProfileActivity.this, ProfileActivity.class);
            }
        });
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

    private void removeFriend() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String username = getIntent().getStringExtra("USERNAME");
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> friendsUIDList = (List<String>) documentSnapshot.getData().get("friends");
                        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                        CollectionReference colRef = rootRef.collection("users");
                        Query nameQuery = colRef.whereEqualTo("username", username);
                        nameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String friendId = document.getId();
                                        Log.d("user", friendId);
                                        DocumentReference docRef = db.collection("users")
                                                .document(user.getUid());
                                        docRef.update("friends", FieldValue.arrayRemove(friendId));

                                    }
                                }
                            }
                        });

                    }
                });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String currentID = user.getUid();
//        DocumentReference reference;
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//        reference = ;
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