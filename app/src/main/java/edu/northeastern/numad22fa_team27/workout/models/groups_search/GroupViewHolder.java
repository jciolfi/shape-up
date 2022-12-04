package edu.northeastern.numad22fa_team27.workout.models.groups_search;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import edu.northeastern.numad22fa_team27.R;

public class GroupViewHolder extends RecyclerView.ViewHolder {
    public TextView groupName;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        this.groupName = itemView.findViewById(R.id.group_item);
    }
}
