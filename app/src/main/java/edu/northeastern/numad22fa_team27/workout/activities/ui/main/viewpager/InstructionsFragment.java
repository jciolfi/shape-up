package edu.northeastern.numad22fa_team27.workout.activities.ui.main.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import edu.northeastern.numad22fa_team27.R;

public class InstructionsFragment extends BottomSheetDialogFragment {
    private String instructions;


    public InstructionsFragment() {
        //instructions =
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.instructions_layout, container, false);

        TextView textView = view.findViewById(R.id.txt_instructions_desc);
        textView.setText(instructions);
        ImageButton close = view.findViewById(R.id.btn_close_instructions);
        close.setOnClickListener(view1 -> {
            Objects.requireNonNull(getDialog()).dismiss();
        });

        return view;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
