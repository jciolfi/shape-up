package edu.northeastern.numad22fa_team27.sticker_messenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad22fa_team27.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sendView = inflater.inflate(R.layout.fragment_friends, container, false);

        // Add callbacks
        final Button sendButton = sendView.findViewById(R.id.sticker_send_button);
        sendButton.setOnClickListener(c -> {
            // Destroy this fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(this)
                    .commit();
        });

        return sendView;
    }
}