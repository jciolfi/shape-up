package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class MessageHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView lastMessage;

    public MessageHolder(View view) {
        super(view);
        views(view);
    }

    private void views(View view) {
        title = view.findViewById(R.id.txt_chat_title);
        lastMessage = view.findViewById(R.id.txt_first_message);
    }
}
