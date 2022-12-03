package edu.northeastern.numad22fa_team27.workout.models.user_search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private final List<UserDAO> users;

    public UserAdapter(List<UserDAO> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_user, null));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String username = users.get(position).username;
        holder.username.setText(username);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
