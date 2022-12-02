package edu.northeastern.numad22fa_team27.workout.models.user_search;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView username;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        this.username = itemView.findViewById(R.id.user_item);
    }
}
