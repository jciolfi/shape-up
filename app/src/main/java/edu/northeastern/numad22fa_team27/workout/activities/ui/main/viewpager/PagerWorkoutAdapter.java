package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;

public class PagerWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<WorkoutCard> workouts;
    private int currentIndex;

    public PagerWorkoutAdapter(List<WorkoutCard> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_workout,
                viewGroup, false);
//        view.setOnClickListener(v -> {
//            if (v.getId() == R.id.btn_instructions){
//                workouts.get(currentIndex).getDescription();
//            } else if (v.getId() == R.id.ckb_completed) {
//                workouts.get(currentIndex).setIsComplete();
//            }
//        });
        ViewPagerHolder vph = new ViewPagerHolder(view);
        //vph
        return vph;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        WorkoutCard workout = workouts.get(i);
        setInfo((ViewPagerHolder) holder, workout);
        holder.itemView.setOnClickListener(view -> {
            currentIndex = Integer.valueOf(i);
        });
    }

    public String getCurrentInstructions(int index) {
        return workouts.get(index).getDescription();
    }

    private void setInfo(ViewPagerHolder h, WorkoutCard workoutCard) {
        h.title.setText(workoutCard.getTitle());
        h.workoutImg.setImageResource(workoutCard.getImgInt());// .setImageIcon(workoutCard.getWorkoutImage());
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_instructions){
            workouts.get(currentIndex).getDescription();
        } else if (view.getId() == R.id.ckb_completed) {
            workouts.get(currentIndex).setIsComplete();
        }
    }

    private class ViewPagerHolder extends RecyclerView.ViewHolder{

        //public TextView artistName;
        public TextView title;
        public ImageView workoutImg;

        public ViewPagerHolder(View view) {
            super(view);
            views(view);
        }

        private void views(View view) {
            //artistName = view.findViewById(R.id.artistname);
            title = view.findViewById(R.id.txt_workout_title);
            workoutImg = view.findViewById(R.id.img_workout);
        }
    }
}
