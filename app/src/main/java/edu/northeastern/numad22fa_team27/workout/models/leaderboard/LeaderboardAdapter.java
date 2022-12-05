package edu.northeastern.numad22fa_team27.workout.models.leaderboard;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
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
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    private final static String TAG = "LeaderboardAdapter";
    private final List<UserDAO> users;
    private final String[] category;

    public LeaderboardAdapter(List<UserDAO> users, String[] category) {
        this.users = users;
        this.category = category;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LeaderboardViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_leaderboard_user, null));
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        UserDAO user = users.get(position);
        holder.userPlace.setText(String.format("%s.", position+1));
        holder.userEntry.setText(user.username);
        holder.userStreak.setText(String.format("Streak: %s days",
                user.bestCategoryStreaks.get(category[0].toUpperCase())));
        holder.userEntry.setOnClickListener(view -> {
            // build custom popup
            final Dialog userInfoDialog = new Dialog(view.getContext());
            userInfoDialog.setContentView(LayoutInflater.from(view.getContext())
                    .inflate(R.layout.dialog_user_item, (ViewGroup)view.getParent(), false));

            // set title
            TextView usernameTitle = userInfoDialog.findViewById(R.id.title_username);
            usernameTitle.setText(user.username);

            // set profile picture
            ImageView profilePic = userInfoDialog.findViewById(R.id.dialog_profile_pic);
            GetProfilePic getProfilePic = new GetProfilePic(user, profilePic);
            new Thread(getProfilePic).start();

            // set friends count
            TextView friendCount = userInfoDialog.findViewById(R.id.txt_friend_count);
            friendCount.setText(String.format("Friends: %s",
                    user.friends == null ? 0 : user.friends.size()));

            // set groups joined count
            TextView groupCount = userInfoDialog.findViewById(R.id.txt_group_count);
            groupCount.setText(String.format("Groups Joined: %s",
                    user.joinedGroups == null ? 0 : user.joinedGroups.size()));

            // set up close button
            Button closeButton = userInfoDialog.findViewById(R.id.btn_close_user);
            closeButton.setOnClickListener(view1 -> userInfoDialog.dismiss());

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

    private static class GetProfilePic implements Runnable {
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
