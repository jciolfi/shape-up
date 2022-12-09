package edu.northeastern.numad22fa_team27.workout.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;

/**
 * this is the chat adapter which allows a recycler to
 * control the components in a recycler.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {

    private final List<ChatCard> list;

    public ChatAdapter(List<ChatCard> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_chat_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ChatCard card = list.get(position);
        holder.title.setText(card.getUserName());
        holder.body.setText(card.getBody());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
