package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class StickerSendModel extends ViewModel {
    private final MutableLiveData<Pair<String, String>> selectedItem = new MutableLiveData<>();
    public void selectItem(Pair<String, String> item) {
        selectedItem.setValue(item);
    }

    public LiveData<Pair<String, String>> getSelectedItem() {
        return selectedItem;
    }
}
