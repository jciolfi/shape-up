package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private final List<MessageCard> list;

    public MessageAdapter(List<MessageCard> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MessageHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_cards, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder h, int i) {
        MessageCard cards = list.get(i);
        getInfo(h, cards);
    }

    private void getInfo(MessageHolder h, MessageCard cards) {
        h.title.setText(cards.getTitle());
        h.lastMessage.setText(cards.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
