package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class UserFriendHolder  extends RecyclerView.ViewHolder {
    public ImageView friendPicture;
    public TextView friendName;
    public TextView messagePreview;

    public UserFriendHolder(View view) {
        super(view);
        views(view);
    }

    private void views(View view) {
        friendPicture = view.findViewById(R.id.profile_image);
        friendName = view.findViewById(R.id.userFriendName);
        messagePreview  = view.findViewById(R.id.userFriendMessagePreview);
    }
}
