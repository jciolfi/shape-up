package edu.northeastern.numad22fa_team27.sticker_messenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";
    private static final String ARG_PARAM1 = "friendsList";
    private static final String ARG_PARAM2 = "stickerOptions";

    private List<String> friendsList;
    private List<String> stickerOptions;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendsList Parameter 1.
     * @param stickerOptions Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance(List<String> friendsList, List<String> stickerOptions) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, (ArrayList<String>) friendsList);
        args.putStringArrayList(ARG_PARAM2, (ArrayList<String>) stickerOptions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friendsList = getArguments().getStringArrayList(ARG_PARAM1);
            stickerOptions = getArguments().getStringArrayList(ARG_PARAM2);
        } else {
            friendsList = new ArrayList<>();
            stickerOptions = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sendView = inflater.inflate(R.layout.fragment_friends, container, false);

        // Friends spinner
        Spinner friends = sendView.findViewById(R.id.friends_spinner);
        ArrayAdapter<String> friendAdapter = new ArrayAdapter<>(sendView.getContext(),
                android.R.layout.simple_spinner_item, friendsList);
        friendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        friends.setAdapter(friendAdapter);
        friends.setSelection(0);

        // Sticker spinner
        Spinner stickers = sendView.findViewById(R.id.sticker_spinner);
        ArrayAdapter<String> stickerAdapter = new ArrayAdapter<>(sendView.getContext(),
                android.R.layout.simple_spinner_item, stickerOptions);
        stickerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stickers.setAdapter(stickerAdapter);
        stickers.setSelection(0);

        Log.v(TAG, String.format("We have %d friends and %d possible stickers", friendsList.size(), stickerOptions.size()));

        // Add callbacks
        final Button sendButton = sendView.findViewById(R.id.sticker_send_button);
        sendButton.setOnClickListener(c -> {
            // Hide this fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(this)
                    .commit();
        });

        final Button cancelButton = sendView.findViewById(R.id.sticker_cancel_button);
        cancelButton.setOnClickListener(c -> {
            // Hide this fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(this)
                    .commit();
        });

        Log.v(TAG, "Created!");

        return sendView;
    }
}