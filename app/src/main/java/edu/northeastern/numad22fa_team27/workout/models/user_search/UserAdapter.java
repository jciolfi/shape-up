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

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private final String TAG = "UserAdapter";
    private final List<UserDAO> users;
    private final ViewGroup container;
    private final View searchView;
    private final FirebaseUser currentUser;

    public UserAdapter(List<UserDAO> users, ViewGroup container, View searchView, FirebaseUser currentUser) {
        this.users = users;
        this.container = container;
        this.searchView = searchView;
        this.currentUser = currentUser;
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
        UserDAO user = users.get(position);
        holder.username.setText(user.username);
        GetProfilePic getProfilePic = new GetProfilePic(user, holder.profilePic);
        new Thread(getProfilePic).start();

        holder.username.setOnClickListener(view -> {
            // build custom popup
            final Dialog userInfoDialog = new Dialog(searchView.getContext());
            userInfoDialog.setContentView(LayoutInflater.from(searchView.getContext())
                    .inflate(R.layout.dialog_user_item, container, false));

            // set title
            TextView usernameTitle = userInfoDialog.findViewById(R.id.title_username);
            usernameTitle.setText(user.username);

            // set profile picture
            ImageView dialogProfilePic = userInfoDialog.findViewById(R.id.dialog_profile_pic);
            dialogProfilePic.setImageDrawable(holder.profilePic.getDrawable());

            // set friends count
            TextView friendCount = userInfoDialog.findViewById(R.id.txt_friend_count);
            friendCount.setText(String.format("Friends: %s", user.friends.size()));

            // set groups joined count
            TextView groupCount = userInfoDialog.findViewById(R.id.txt_group_count);
            groupCount.setText(String.format("Groups Joined: %s", user.joinedGroups.size()));

            // set up close button
            Button closeButton = userInfoDialog.findViewById(R.id.btn_close_user);
            closeButton.setOnClickListener(view1 -> {
                userInfoDialog.dismiss();

                // focus will go to search view and bring up keyboard - disable this
                final View groupsView = searchView.findViewById(R.id.rv_users);
                groupsView.requestFocus();
            });

            // TODO set add/remove friend button functionality
            // if friends -> set button to remove friend
            // if not friends -> keep button as add friend
            // if self -> hide button
//            Button actionButton = searchView.findViewById(R.id.btn_friend_action);

            userInfoDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    private class GetProfilePic implements Runnable {
        private final UserDAO user;
        private final ImageView profilePic;

        protected GetProfilePic(UserDAO user, ImageView profilePic) {
            this.user = user;
            this.profilePic = profilePic;
        }

        @Override
        public void run() {
            try {
                InputStream is = (InputStream) new URL(user.profilePic).getContent();
                Drawable avatar = Drawable.createFromStream(is, user.username);
                profilePic.post(() -> profilePic.setImageDrawable(avatar));
            } catch (Exception e) {
                Log.w(TAG, e.toString());
            }
        }
    }
}
