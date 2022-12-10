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
    private final Map<Integer, ChatCard> list;
    private final List<ChatCard> finalizedListData;

    public AsyncChatAdapter() {
        this.finalizedListData = new ArrayList<>();
        this.list = new TreeMap<>();
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

    public ChatCard getCardAtPositionOrDefault(Integer position) {
        return list.getOrDefault(position, new ChatCard("", ""));
    }

    public void setCardAtPosition(Integer position, ChatCard card) {
        list.put(position, card);

        // Recompile list of data
        this.finalizedListData.clear();

        // TreeMap is always ordered
        for (ChatCard c : list.values()) {
            this.finalizedListData.add(c);
        }
    }

    public List<ChatCard> getCards() {
        return this.finalizedListData;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
