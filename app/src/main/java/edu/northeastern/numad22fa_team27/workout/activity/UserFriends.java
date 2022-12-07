package edu.northeastern.numad22fa_team27.workout.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.InterItemSpacer;
import edu.northeastern.numad22fa_team27.workout.adapters.UserFriendAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.UserWorkoutAdapter;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutProgress;
import edu.northeastern.numad22fa_team27.workout.test_utilities.FakeWorkoutGenerator;

public class UserFriends extends AppCompatActivity {
    private final static String TAG = "UserFriends";
    private final List<User> originalFriendData = new ArrayList<>();
    private final List<User> displayedFriendData = new ArrayList<>();

    private void genFakeFriends() {
        for (int i = 0; i < 20; i++) {
            originalFriendData.add(new User(String.format("Friend %d", i + 1), ""));
        }
    }

    private List<User> searchFriends(String targetString) {
        String searchString = targetString.toLowerCase();
        return originalFriendData.stream()
                .filter(k -> k.getUsername().toLowerCase().contains(searchString))
                .collect(Collectors.toList());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        RecyclerView lists = findViewById(R.id.userFriendsRecView);
        lists.setHasFixedSize(true);
        lists.setAdapter(new UserFriendAdapter(displayedFriendData));
        lists.setLayoutManager(manager);
        lists.addItemDecoration(new DividerItemDecoration(lists.getContext(), DividerItemDecoration.VERTICAL));

        // Create generated friends
        genFakeFriends();

        // Load data
        displayedFriendData.addAll(originalFriendData);
        Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();

        EditText userWorkoutSearch = findViewById(R.id.userFriendSearch);
        userWorkoutSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                displayedFriendData.clear();
                displayedFriendData.addAll(searchFriends(s.toString()));
                Objects.requireNonNull(lists.getAdapter()).notifyDataSetChanged();
            }
        });

    }
}