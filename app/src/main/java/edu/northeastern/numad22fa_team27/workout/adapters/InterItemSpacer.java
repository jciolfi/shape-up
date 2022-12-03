package edu.northeastern.numad22fa_team27.workout.adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InterItemSpacer extends RecyclerView.ItemDecoration{
    private final int pad = 12;

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final int itemCount = state.getItemCount();

        /** first position */
        if (itemPosition == 0) {
            outRect.set(pad, pad, 0, pad);
        }
        /** last position */
        else if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.set(0, pad, pad, pad);
        }
        /** positions between first and last */
        else {
            outRect.set(0, pad, 0, pad);
        }
    }
}
