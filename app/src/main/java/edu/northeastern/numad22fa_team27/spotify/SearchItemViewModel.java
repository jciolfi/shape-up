package edu.northeastern.numad22fa_team27.spotify;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchItemViewModel extends ViewModel {
    private final MutableLiveData<SearchItem> selectedItem = new MutableLiveData<>();
    public void selectItem(SearchItem item) {
        selectedItem.setValue(item);
    }

    public LiveData<SearchItem> getSelectedItem() {
        return selectedItem;
    }
}

