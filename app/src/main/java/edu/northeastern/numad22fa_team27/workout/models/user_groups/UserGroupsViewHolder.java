package edu.northeastern.numad22fa_team27.workout.models.user_groups;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class UserGroupsViewHolder extends RecyclerView.ViewHolder {
    public TextView groupName;
    public TextView numMembers;
    public Switch publicSwitch;

    public UserGroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.groupName = itemView.findViewById(R.id.user_group_name);
        this.numMembers = itemView.findViewById(R.id.user_group_member_count);
        this.publicSwitch = itemView.findViewById(R.id.user_group_public);
    }
}
