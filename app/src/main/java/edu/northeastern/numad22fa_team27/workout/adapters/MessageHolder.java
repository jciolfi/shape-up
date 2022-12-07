package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class MessageHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView lastMessage;
    public MessageClickListener listener;

    public MessageHolder(View view, MessageClickListener listener) {
        super(view);

        this.listener = listener;
        this.title = view.findViewById(R.id.txt_chat_title);
        this.lastMessage = view.findViewById(R.id.txt_first_message);

        view.findViewById(R.id.id_message_card_base).setOnClickListener(view1 -> {
            listener.onClick(view1, getLayoutPosition());
        });
    }
}
