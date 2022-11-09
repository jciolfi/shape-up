package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class MessageHolder extends RecyclerView.ViewHolder{
    public ImageView sticker;
    public TextView user;
    public TextView date;

    public MessageHolder(View view) {
        super(view);
        views(view);
    }

    private void views(View view) {
        sticker = view.findViewById(R.id.sticker);
        user = view.findViewById(R.id.user);
        date = view.findViewById(R.id.date);
    }
}
