package edu.northeastern.numad22fa_team27.workout.activity;

import static edu.northeastern.numad22fa_team27.Util.requestNoActivityBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.interfaces.IRecyclerViewCardsClickable;
import edu.northeastern.numad22fa_team27.workout.models.FriendsAdapter;
import edu.northeastern.numad22fa_team27.workout.models.FriendsCard;

public class MyFriendsActivity extends AppCompatActivity implements IRecyclerViewCardsClickable {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<FriendsCard> list;
    FriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNoActivityBar(this);
        setContentView(R.layout.activity_my_friends);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        initData();
        initRecyclerView();

    }

    private void initData() {
        list = new ArrayList<>();
        db.collection("users")
                .document(user.getUid())
                        .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        List<String> friendsUIDList = (List<String>) documentSnapshot.getData().get("friends");
                                        for (String id : friendsUIDList) {
                                            db.collection("users")
                                                    .document(id)
                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            String username = documentSnapshot.getString("username");
                                                            String url = documentSnapshot.getString("profilePic");
                                                            Log.d("ReadingFS", "onSuccess: " + documentSnapshot.getString("username"));
                                                            list.add(new FriendsCard(url, username));
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                        }
                                    }
                                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.friendsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendsAdapter(list, this);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MyFriendsActivity.this, FriendProfileActivity.class);
        intent.putExtra("USERNAME", list.get(position).getUsername());
        intent.putExtra("PROFILEPIC", list.get(position).getImageView());
        startActivity(intent);
    }
}