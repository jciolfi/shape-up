package edu.northeastern.numad22fa_team27.workout.models.leaderboard;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
    public TextView userEntry;

    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        this.userEntry = itemView.findViewById(R.id.user_item);
    }
}
