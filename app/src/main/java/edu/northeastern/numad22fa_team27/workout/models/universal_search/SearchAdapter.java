package edu.northeastern.numad22fa_team27.workout.models.universal_search;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.interfaces.Summarizeable;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {

    private final List<Summarizeable> list;
    private SearchClickListener listener;
    private final int height;

    public SearchAdapter(List<Summarizeable> list, SearchClickListener listener) {
        this.listener = listener;
        this.list = list;
        this.height = 512;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SearchHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_card, viewGroup, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder h, int i) {
        Summarizeable card = list.get(i);

        h.searchResultTitle.setText(card.getTitle());
        h.searchResultMiscText.setText(card.getMisc());

        if (card.getImage() != null) {
            Picasso.get()
                    .load(card.getImage())
                    .placeholder(R.drawable.workout_icon)
                    .error(R.drawable.workout_icon)
                    .resize(height, height)
                    .centerCrop(Gravity.CENTER)
                    .noFade()
                    .into(h.searchResultIcon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}