package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.northeastern.numad22fa_team27.R;

/**
 * this is the chat adapter which allows a recycler to
 * control the components in a recycler.
 */
public class AsyncChatAdapter extends RecyclerView.Adapter<ChatHolder> {
    private final List<ChatCard> finalizedListData;

    public AsyncChatAdapter(List<ChatCard> data) {
        this.finalizedListData = data;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_chat_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ChatCard card = finalizedListData.get(position);
        holder.title.setText(card.getUserName());
        holder.body.setText(card.getBody());
    }

    @Override
    public int getItemCount() {
        return finalizedListData.size();
    }
}
