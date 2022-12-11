package edu.northeastern.numad22fa_team27.workout.models.user_groups;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class UserGroupsViewHolder extends RecyclerView.ViewHolder {
    public TextView groupName;
    public TextView numMembers;
    public Switch publicSwitch;
    public Button leaveButton;
    public ConstraintLayout cardBase;

    public UserGroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.groupName = itemView.findViewById(R.id.user_group_name);
        this.numMembers = itemView.findViewById(R.id.user_group_member_count);
        this.publicSwitch = itemView.findViewById(R.id.user_group_public);
        this.leaveButton = itemView.findViewById(R.id.btn_leave_group);
        this.cardBase = itemView.findViewById(R.id.group_card_layout);
    }
}
