package edu.northeastern.numad22fa_team27.workout.models.groups_search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private final List<GroupDAO> displayGroups;

    public GroupAdapter(List<GroupDAO> displayGroups) {
        this.displayGroups = displayGroups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_group, null));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupDAO group = displayGroups.get(position);
        holder.groupName.setText(group.groupName + " " + group.members.size());
    }

    @Override
    public int getItemCount() {
        return displayGroups.size();
    }
}
