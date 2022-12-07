package edu.northeastern.numad22fa_team27.workout.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.SearchItemViewModel;
import edu.northeastern.numad22fa_team27.workout.models.ChatItem;
import edu.northeastern.numad22fa_team27.workout.models.ChatItemViewModel;

public class NewGroupChatFragment extends Fragment {
    private String[] listOfFriends; // this is the current list of friends of the user
    private String[] addedArray; // this will go with the text view
    private ChatItemViewModel viewModel;

    public NewGroupChatFragment(String[] listOfFriends) {
        //requrired empty public constructor
        //add here list of friends of users?
        this.listOfFriends = listOfFriends;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newGroupChatView = inflater.inflate(R.layout.fragment_add_chat, container, false);

        addedArray = new String[0];

        viewModel = new ViewModelProvider(requireActivity()).get(ChatItemViewModel.class);

        //Friends spinner
        Spinner friends = newGroupChatView.findViewById(R.id.spn_friends);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(newGroupChatView.getContext(),
                android.R.layout.simple_spinner_item, listOfFriends);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        friends.setAdapter(adapter);
        friends.setSelection(0);

        //Friends on list Text
        TextView addedFriends = newGroupChatView.findViewById(R.id.txt_added_friends);

        //Add button
        Button addFriend = newGroupChatView.findViewById(R.id.btn_add_friend);
        addFriend.setOnClickListener(c -> {
            String[] newAdded = new String[addedArray.length + 1];
            for (int i = 0; i < addedArray.length; i++) {
                if (addedArray[i].equals(friends.getSelectedItem().toString())) {
                    addFriend.setError("friend already in list");
                    return;//check if the user is already added and exit out of set on clicker
                }
                newAdded[i] = addedArray[i];
            }
            newAdded[addedArray.length] = friends.getSelectedItem().toString();
            addedArray = newAdded;
            String resultText = "";
            for (String s : addedArray) {
                resultText += s + ", ";
            }
            addedFriends.setText(resultText);

        });

        //Create Chat button

        final Button createChatButton = newGroupChatView.findViewById(R.id.btn_add_chat);
        createChatButton.setOnClickListener(c -> {
            // TODO: More input validation

            if (addedArray.length == 0) {
                addedFriends.setError("Please add one more friend");
                return;
            }
            friends.setSelection(0);
            addedFriends.setText("Add Friends");
            viewModel.selectItem(new ChatItem(addedArray));

            //
            /*InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            Editable
            imm.hideSoftInputFromWindow(, 0);*/
        });
        return newGroupChatView;
    }

}
