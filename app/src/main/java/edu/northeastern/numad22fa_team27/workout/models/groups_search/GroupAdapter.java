package edu.northeastern.numad22fa_team27.workout.models.groups_search;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private final List<GroupDAO> displayGroups;
    private final ViewGroup container;
    private final View searchView;

    public GroupAdapter(List<GroupDAO> displayGroups, ViewGroup container, View searchView) {
        this.displayGroups = displayGroups;
        this.container = container;
        this.searchView = searchView;
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
        holder.groupName.setText(group.groupName);
        holder.groupName.setOnClickListener(view -> {
            // build custom popup
            final Dialog groupInfoDialog = new Dialog(searchView.getContext());
            groupInfoDialog.setContentView(LayoutInflater.from(searchView.getContext())
                    .inflate(R.layout.dialog_group_item, container, false));

            // set title
            TextView groupTitle = groupInfoDialog.findViewById(R.id.title_group_name);
            groupTitle.setText(group.groupName);

            // set member info
            TextView memberInfo = groupInfoDialog.findViewById(R.id.txt_member_info);
            memberInfo.setText(String.format("Members: %s", group.members.size()));

            // set up close button
            Button closeButton = groupInfoDialog.findViewById(R.id.btn_close_group);
            closeButton.setOnClickListener(view1 -> {
                groupInfoDialog.dismiss();

                // focus will go to search view and bring up keyboard - disable this
                final View groupsView = searchView.findViewById(R.id.rv_groups);
                groupsView.requestFocus();
            });

            // TODO set up join button
//            Button joinButton = groupInfoDialog.findViewById(R.id.btn_join_group);
//            joinButton.setOnClickListener(v -> {
//                // new FirestoreService().joinGroup();
//            });
            groupInfoDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return displayGroups.size();
    }
}
