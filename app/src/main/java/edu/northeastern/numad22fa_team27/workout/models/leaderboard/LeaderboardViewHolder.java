package edu.northeastern.numad22fa_team27.workout.models.leaderboard;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
    public TextView userPlace;
    public TextView userEntry;
    public TextView userStreak;
    public CardView background;

    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        this.background = itemView.findViewById(R.id.leaderboard_card_base);
        this.userPlace = itemView.findViewById(R.id.user_place);
        this.userEntry = itemView.findViewById(R.id.user_item);
        this.userStreak = itemView.findViewById(R.id.user_streak);
    }
}
