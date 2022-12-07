package edu.northeastern.numad22fa_team27.workout.models.user_search;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private final String TAG = "UserAdapter";
    private final List<User> users;
    private final ViewGroup container;
    private final View searchView;
    private final FirestoreService firestoreService;
    private final String currentUserID;

    public UserAdapter(List<User> users, ViewGroup container, View searchView, FirestoreService firestoreService, String currentUserID) {
        this.users = users;
        this.container = container;
        this.searchView = searchView;
        this.firestoreService = firestoreService;
        this.currentUserID = currentUserID;
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
        User user = users.get(position);
        holder.username.setText(user.getUsername());
        GetProfilePic getProfilePic = new GetProfilePic(user, holder.profilePic);
        new Thread(getProfilePic).start();

        holder.username.setOnClickListener(view -> {
            // build custom popup
            final Dialog userInfoDialog = new Dialog(searchView.getContext());
            userInfoDialog.setContentView(LayoutInflater.from(searchView.getContext())
                    .inflate(R.layout.dialog_user_item, container, false));

            // set title
            TextView usernameTitle = userInfoDialog.findViewById(R.id.title_username);
            usernameTitle.setText(user.getUsername());

            // set profile picture
            ImageView dialogProfilePic = userInfoDialog.findViewById(R.id.dialog_profile_pic);
            dialogProfilePic.setImageDrawable(holder.profilePic.getDrawable());

            // set friends count
            TextView friendCount = userInfoDialog.findViewById(R.id.txt_friend_count);
            friendCount.setText(String.format("Friends: %s",
                    user.getFriends() == null ? 0 : user.getFriends().size()));

            // set groups joined count
            TextView groupCount = userInfoDialog.findViewById(R.id.txt_group_count);
            groupCount.setText(String.format("Groups Joined: %s",
                    user.getJoinedGroups() == null ? 0 : user.getJoinedGroups().size()));

            // set up close button
            Button closeButton = userInfoDialog.findViewById(R.id.btn_close_user);
            closeButton.setOnClickListener(view1 -> {
                userInfoDialog.dismiss();

                // focus will go to search view and bring up keyboard - disable this
                final View groupsView = searchView.findViewById(R.id.rv_users);
                groupsView.requestFocus();
            });

            Button actionButton = userInfoDialog.findViewById(R.id.btn_friend_action);
            actionButton.setEnabled(true);
            // if self -> hide positive button
            if (user.getUserID().equals(currentUserID) || user.getFriends() == null) {
                actionButton.setVisibility(View.INVISIBLE);
            } else {
                actionButton.setVisibility(View.VISIBLE);
                if (user.getFriends().contains(currentUserID)) {
                    // already friends -> change button to remove friend
                    actionButton.setText("Remove");
                    actionButton.setOnClickListener(removeView -> {
                        firestoreService.tryRemoveFriend(user.getUserID());
                        closeButton.callOnClick();
                    });
                } else if (user.getIncomingFriendRequests().contains(currentUserID)) {
                    actionButton.setText("Requested");
                    actionButton.setEnabled(false);
                } else {
                    // not friends and not requested -> change button to add friend
                    actionButton.setText("Add");
                    actionButton.setOnClickListener(addView -> {
                        actionButton.setText("Requested");
                        actionButton.setEnabled(false);
                        firestoreService.tryRequestFriend(user.getUserID());
                        closeButton.callOnClick();
                    });
                }
            }

            userInfoDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    private class GetProfilePic implements Runnable {
        private final User user;
        private final ImageView profilePic;

        protected GetProfilePic(User user, ImageView profilePic) {
            this.user = user;
            this.profilePic = profilePic;
        }

        @Override
        public void run() {
            try {
                InputStream is = (InputStream) new URL(user.getProfilePic()).getContent();
                Drawable avatar = Drawable.createFromStream(is, user.getUsername());
                profilePic.post(() -> profilePic.setImageDrawable(avatar));
            } catch (Exception e) {
                Log.w(TAG, e.toString());
            }
        }
    }
}
