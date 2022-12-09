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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.callbacks.GetUserByIDCallback;
import edu.northeastern.numad22fa_team27.workout.callbacks.UpdateUserDialogFromSelf;
import edu.northeastern.numad22fa_team27.workout.models.User;
import edu.northeastern.numad22fa_team27.workout.services.FirestoreService;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private final String TAG = "UserAdapter";
    private final List<User> users;
    private final ViewGroup container;
    private final View searchView;
    private final FirestoreService firestoreService;
    private final User currentUser = new User();

    public UserAdapter(List<User> users, ViewGroup container, View searchView, FirestoreService firestoreService) {
        this.users = users;
        this.container = container;
        this.searchView = searchView;
        this.firestoreService = firestoreService;

        // set current user
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            firestoreService.getUserByID(fbUser.getUid(), new GetUserByIDCallback(currentUser));
        }
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
        User otherUser = users.get(position);
        holder.username.setText(otherUser.getUsername());
        GetProfilePic getProfilePic = new GetProfilePic(otherUser, holder.profilePic);
        new Thread(getProfilePic).start();

        holder.username.setOnClickListener(view -> {
            // build custom popup
            final Dialog userInfoDialog = new Dialog(searchView.getContext());
            userInfoDialog.setContentView(LayoutInflater.from(searchView.getContext())
                    .inflate(R.layout.dialog_user_item, container, false));

            // set title
            TextView usernameTitle = userInfoDialog.findViewById(R.id.title_username);
            usernameTitle.setText(otherUser.getUsername());

            // set profile picture
            ImageView dialogProfilePic = userInfoDialog.findViewById(R.id.dialog_profile_pic);
            dialogProfilePic.setImageDrawable(holder.profilePic.getDrawable());

            // set friends count
            TextView friendCount = userInfoDialog.findViewById(R.id.txt_friend_count);
            friendCount.setText(String.format("Friends: %s",
                    otherUser.getFriends() == null ? 0 : otherUser.getFriends().size()));

            // set groups joined count
            TextView groupCount = userInfoDialog.findViewById(R.id.txt_group_count);
            groupCount.setText(String.format("Groups Joined: %s",
                    otherUser.getJoinedGroups() == null ? 0 : otherUser.getJoinedGroups().size()));

            // set up close button / dismiss listener
            Button closeButton = userInfoDialog.findViewById(R.id.btn_close_user);
            userInfoDialog.setOnDismissListener(dialogInterface -> {
                // focus will go to search view and bring up keyboard - disable this
                final TextView sortBy = searchView.findViewById(R.id.txt_user_sort);
                sortBy.requestFocus();
            });
            closeButton.setOnClickListener(view1 -> {
                userInfoDialog.dismiss();
            });

            // query db to get the user state to update action button
            Button actionButton = userInfoDialog.findViewById(R.id.btn_friend_action);
            firestoreService.getUserByID(currentUser.getUserID(),
                    new UpdateUserDialogFromSelf(currentUser, otherUser, actionButton, firestoreService, userInfoDialog));

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
