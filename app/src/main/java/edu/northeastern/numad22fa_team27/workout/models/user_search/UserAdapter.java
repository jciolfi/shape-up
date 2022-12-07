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

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.GetUserByIDCallback;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private final String TAG = "UserAdapter";
    private final List<User> users;
    private final ViewGroup container;
    private final View searchView;
    private final FirestoreService firestoreService;
    private final User currentUser = new User();

    public UserAdapter(List<User> users, ViewGroup container, View searchView, FirestoreService firestoreService, String currentUserID) {
        this.users = users;
        this.container = container;
        this.searchView = searchView;
        this.firestoreService = firestoreService;

        // set current user
        firestoreService.getUserByID(currentUserID, new GetUserByIDCallback(currentUser));
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
            actionButton.setVisibility(View.VISIBLE);

            // query db to get the user state
            firestoreService.getUserByID(user.getUserID(), new GetUserByIDCallback(user));
            firestoreService.getUserByID(currentUser.getUserID(), new GetUserByIDCallback(currentUser));

            /**
             * Logic overview:
             * - if self -> hide action button
             * - if friends -> change to remove
             * - if not friends and user has requested currentUser-> change to accept
             * - if not friends and incoming request present -> change to add
             */
            if (user.getUserID().equals(currentUser.getUserID())) {
                actionButton.setVisibility(View.INVISIBLE);
            } else if (user.getFriends().contains(currentUser.getUserID())) {
                actionButton.setText("Remove");
                actionButton.setOnClickListener(removeView -> {
                    firestoreService.removeFriend(user.getUserID());
                    closeButton.callOnClick();
                });
            } else if (user.getIncomingFriendRequests().contains(currentUser.getUserID())) {
                actionButton.setText("Requested");
                actionButton.setEnabled(false);
            } else if (currentUser.getIncomingFriendRequests().contains(user.getUserID())) {
                actionButton.setText("Accept");
                actionButton.setOnClickListener(addView -> {
                    firestoreService.tryAcceptFriendRequest(user.getUserID());
                    closeButton.callOnClick();
                });
            } else {
                actionButton.setText("Add");
                actionButton.setOnClickListener(addView -> {
                    firestoreService.tryRequestFriend(user.getUserID());
                    closeButton.callOnClick();
                });
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
