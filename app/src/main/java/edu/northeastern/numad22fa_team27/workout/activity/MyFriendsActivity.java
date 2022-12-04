package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.FriendsAdapter;
import edu.northeastern.numad22fa_team27.workout.models.FriendsCard;

public class MyFriendsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private List<FriendsCard> list;
    FriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        initData();
        initRecyclerView();

    }

    private void initData() {
        list = new ArrayList<>();

        list.add(new FriendsCard(R.drawable.defaultpic, "farzad"));
        list.add(new FriendsCard(R.drawable.defaultpic, "nasim"));
        list.add(new FriendsCard(R.drawable.defaultpic, "hasan"));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.friendsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendsAdapter(list);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}