package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private final List<Message> list;
    private MessageClickListener listener;

    public MessageAdapter(List<Message> list, MessageClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MessageHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.message_card, viewGroup, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder h, int i) {
        Message cards = list.get(i);
        getInfo(h, cards);
    }

    private void getInfo(MessageHolder h, Message cards) {
        h.title.setText(cards.getChatID());
        h.lastMessage.setText(cards.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
