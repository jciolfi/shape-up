package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Cards;
import edu.northeastern.numad22fa_team27.spotify.spotifyviews.Holder;
import edu.northeastern.numad22fa_team27.workout.models.Workout;

public class PagerWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WorkoutCard> workouts;

    public PagerWorkoutAdapter(List<WorkoutCard> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_workout,
                viewGroup, false);
        return new ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        WorkoutCard workout = workouts.get(i);
        getInfo((ViewPagerHolder) holder, workout);
    }

    private void getInfo(ViewPagerHolder h, WorkoutCard workoutCard) {
        h.title.setText(workoutCard.getTitle());
        h.workoutImg.setImageResource(workoutCard.getImgInt());// .setImageIcon(workoutCard.getWorkoutImage());
    }

    @Override
    public int getItemCount() {
        return workouts.size();
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
