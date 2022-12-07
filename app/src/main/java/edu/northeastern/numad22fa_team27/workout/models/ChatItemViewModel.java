package edu.northeastern.numad22fa_team27.workout.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.northeastern.numad22fa_team27.spotify.SearchItem;

public class ChatItemViewModel extends ViewModel {
    private final MutableLiveData<ChatItem> selectedItem = new MutableLiveData<>();
    public void selectItem(ChatItem item) {
        selectedItem.setValue(item);
    }

    public LiveData<ChatItem> getSelectedItem() {return selectedItem;}
}
