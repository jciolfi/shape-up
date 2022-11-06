package edu.northeastern.numad22fa_team27.sticker_messenger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.List;

import edu.northeastern.numad22fa_team27.R;

public class SendFragment extends Fragment {

    private List<String> friendsList;

    public SendFragment(List<String> friends) {
        this.friendsList = friends;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View searchView = inflater.inflate(R.layout.fragment_send, container, false);

        // Set up sticker dropdown
        // TODO - don't hardcode this
        String[] stickerString = new String[] {
                "sticker 1", "sticker 2", "sticker 3", "sticker 4", "sticker 5"
        };
        Spinner stickers = searchView.findViewById(R.id.spn_sticker_dd);
        ArrayAdapter<String> adapterOne = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item, stickerString);
        adapterOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stickers.setAdapter(adapterOne);
        stickers.setSelection(0);

        // Set up friend dropdown
        // TODO - don't hardcode this

        String[] input;
        if (friendsList.size() != 0) {
            input = (String[]) friendsList.toArray();
        } else {
            input = new String[]{};
        }

        Spinner friends = searchView.findViewById(R.id.spn_friend_dd);
        ArrayAdapter<String> adapterTwo = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item, input);
        adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        friends.setAdapter(adapterTwo);
        friends.setSelection(0);

        return searchView;
    }


}
