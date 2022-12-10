package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class ChatHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView body;
    public MessageClickListener listener;

    public ChatHolder(View view) {
        super(view);

        this.listener = listener;
        this.title = view.findViewById(R.id.txt_username);
        this.body = view.findViewById(R.id.txt_message_content);
    }
}
