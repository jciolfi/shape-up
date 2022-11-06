package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Holder;
import edu.northeastern.numad22fa_team27.sticker_messenger.MessageCards;

public class MessageInfo extends RecyclerView.Adapter<MessageHolder> {

    private final List<MessageCards> list;

    public MessageInfo(List<MessageCards> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MessageHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_message_cards, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder h, int i) {
        MessageCards mCards = list.get(i);
        getInfo(h, mCards);
    }

    private void getInfo(MessageHolder h, MessageCards mCards) {
        h.sticker.setImageResource(mCards.getSticker().imgId);
        h.user.setText(mCards.getContact());
        h.date.setText(mCards.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
