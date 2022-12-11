package edu.northeastern.numad22fa_team27.workout.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.spotify.SearchItemViewModel;
import edu.northeastern.numad22fa_team27.workout.models.ChatItem;
import edu.northeastern.numad22fa_team27.workout.models.ChatItemViewModel;
import edu.northeastern.numad22fa_team27.workout.models.Message;

public class NewGroupChatFragment extends Fragment {
    private Map<String, String> idToNameMap; // this is the current list of friends of the userid
    private ChatItemViewModel viewModel;
    private String userId;

    public NewGroupChatFragment() {

    }

    public NewGroupChatFragment(String userId, Map<String, String> idToNameMap) {
        //required empty public constructor
        //add here list of friends of users?
        this.idToNameMap = idToNameMap;
        this.userId = userId;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newGroupChatView = inflater.inflate(R.layout.fragment_add_chat, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ChatItemViewModel.class);

        //Name of chat
        EditText nameChat = newGroupChatView.findViewById(R.id.txt_name_chat);

        //Friends spinner
        Spinner friends = newGroupChatView.findViewById(R.id.spn_friends);

        if(idToNameMap != null) {
            List<SpannableString> friendsList = new ArrayList<>();
            for (String friendName : idToNameMap.values()) {
                SpannableString coloredTitle = new SpannableString(friendName);
                coloredTitle.setSpan(new ForegroundColorSpan(R.color.md_theme_light_onBackground), 0, coloredTitle.length(), 0);
                friendsList.add(coloredTitle);
            }

            ArrayAdapter<SpannableString> adapter = new ArrayAdapter<>(newGroupChatView.getContext(),
                    android.R.layout.simple_spinner_item, friendsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            friends.setAdapter(adapter);
            friends.setSelection(0);
        }

        friends.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                Log.v("XYZ", "Selected item " + i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.v("XYZ", "Nothing selected");
            }
        });

        //Friends on list Text
        TextView addedFriends = newGroupChatView.findViewById(R.id.txt_added_friends);

        //Add button
        List<String> friendsAlreadyAdded = new ArrayList<>();
        Button addFriend = newGroupChatView.findViewById(R.id.btn_add_friend);
        addFriend.setOnClickListener(c -> {
            if (friends.getSelectedItem() == null) {
                addFriend.setError("Please add a friend so you can invite them to chat!");
                return;
            }
            String selectedFriend = friends.getSelectedItem().toString();

            if (friendsAlreadyAdded.contains(selectedFriend)) {
                addFriend.setError("friend already in list");
                return;
            }

            addedFriends.setText(String.join(", ", friendsAlreadyAdded));

        });
        final Button cancelButton = newGroupChatView.findViewById(R.id.btn_esc_chat);
        cancelButton.setOnClickListener(c -> {
            viewModel.selectItem(new Message("null", "null"));

        });

        // Create Chat button
        final Button createChatButton = newGroupChatView.findViewById(R.id.btn_add_chat);
        createChatButton.setOnClickListener(c -> {
            if (friendsAlreadyAdded.isEmpty()) {
                addedFriends.setError("Please add one more friend");
                return;
            }
            if (nameChat.getText().toString().isEmpty() || nameChat.getText().equals("")) {
                nameChat.setError("Please name your chat");
                return;
            }
            List<String> chatUserids = new ArrayList<>();
            chatUserids.add(userId);
            for (String s: idToNameMap.keySet()) {
                chatUserids.add(s);
            }

            Message message = new Message(UUID.randomUUID().toString(), nameChat.getText().toString(), chatUserids);

            friends.setSelection(0);
            addedFriends.setText("Add Friends");
            viewModel.selectItem(message);
        });
        return newGroupChatView;
    }

}
