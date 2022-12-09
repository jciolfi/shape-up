package edu.northeastern.numad22fa_team27.workout.models.universal_search;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.numad22fa_team27.R;

public class SearchHolder extends RecyclerView.ViewHolder {
    public ImageView searchResultIcon;
    public TextView searchResultTitle;
    public TextView searchResultMiscText;
    public SearchClickListener listener;

    public SearchHolder(View view, SearchClickListener listener) {
        super(view);

        this.listener = listener;
        this.searchResultIcon = view.findViewById(R.id.result_icon);
        this.searchResultTitle = view.findViewById(R.id.search_item_title);
        this.searchResultMiscText  = view.findViewById(R.id.search_item_misc);

        // Add listener for when we click *anywhere* on the card
        view.findViewById(R.id.card_base).setOnClickListener(view1 -> {
            listener.onClick(view1, getLayoutPosition());
        });
    }
}
