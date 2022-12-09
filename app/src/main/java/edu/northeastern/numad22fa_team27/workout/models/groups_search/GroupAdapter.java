package edu.northeastern.numad22fa_team27.workout.models.groups_search;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.UpdateGroupDialogCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.GetUserByIDCallback;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private final List<Group> displayGroups;
    private final ViewGroup container;
    private final View searchView;
    private final FirestoreService firestoreService = new FirestoreService();
    private final User currentUser = new User();

    public GroupAdapter(List<Group> displayGroups, ViewGroup container, View searchView) {
        this.displayGroups = displayGroups;
        this.container = container;
        this.searchView = searchView;

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            firestoreService.getUserByID(fbUser.getUid(), new GetUserByIDCallback(currentUser));
        }
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
        Group group = displayGroups.get(position);
        holder.groupName.setText(group.getGroupName());
        holder.groupName.setOnClickListener(view -> {
            // build custom popup
            final Dialog groupInfoDialog = new Dialog(searchView.getContext());
            groupInfoDialog.setContentView(LayoutInflater.from(searchView.getContext())
                    .inflate(R.layout.dialog_group_item, container, false));

            // set title
            TextView groupTitle = groupInfoDialog.findViewById(R.id.title_group_name);
            groupTitle.setText(group.getGroupName());

            // set member info
            TextView memberInfo = groupInfoDialog.findViewById(R.id.group_member_info);
            memberInfo.setText(String.format("Members: %s", group.getMembers().size()));

            // set mutual friends in group info
            if (currentUser.getFriends() != null) {
                TextView mutualFriends = groupInfoDialog.findViewById(R.id.group_mutual_friends);
                List<String> membersCopy = new ArrayList<>(group.getMembers());
                membersCopy.retainAll(currentUser.getFriends());

                // stretch goal - logic below to get usernames for mutual friends
                // not worth it right now to do extra lookups for usernames
//                String mutuals = "None";
//                if (membersCopy.size() > 2) {
//                    mutuals = String.format("%s, %s, and %s others",
//                            Util.limitLength(membersCopy.get(0), 8),
//                            Util.limitLength(membersCopy.get(1), 8),
//                            membersCopy.size() - 2);
//                } else if (membersCopy.size() > 0) {
//                    mutuals = String.join(" and ", membersCopy);
//                }

                mutualFriends.setText(String.format("Mutual Friends: %s", membersCopy.size()));
            }

            // set up close button
            Button closeButton = groupInfoDialog.findViewById(R.id.btn_close_group);
            groupInfoDialog.setOnDismissListener(dialogInterface -> {
                // focus will go to search view and bring up keyboard - disable this
                final View groupView = searchView.findViewById(R.id.rv_groups);
                groupView.requestFocus();
            });
            closeButton.setOnClickListener(view1 -> {
                groupInfoDialog.dismiss();
            });

            Button actionButton = groupInfoDialog.findViewById(R.id.btn_join_group);
            firestoreService.getGroupByID(group.getGroupID(), new UpdateGroupDialogCallback(
                    group, currentUser.getUserID(), actionButton, searchView, groupInfoDialog, firestoreService));


            groupInfoDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return displayGroups.size();
    }
}
