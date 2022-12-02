package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutProgress;

public class UserFriendAdapter extends RecyclerView.Adapter<UserFriendHolder> {

    private final List<User> list;

    public UserFriendAdapter(List<User> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public UserFriendHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new UserFriendHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_friend_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserFriendHolder h, int i) {
        User card = list.get(i);
        getInfo(h, card);
    }

    private void getInfo(UserFriendHolder h, User card) {
        // TODO: User pictures need to be stored somewhere in our DB
        // TODO: User messages need to be stored somewhere in our DB

        h.friendName.setText(card.getUsername());
        h.messagePreview.setText("Message preview here ....");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}