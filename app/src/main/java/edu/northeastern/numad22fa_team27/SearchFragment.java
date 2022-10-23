package edu.northeastern.numad22fa_team27;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchItemViewModel viewModel;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

        // Set up genre dropdown
        // TODO - don't hardcode this
        String[] genreStrings = new String[] {
                "pop", "rock", "classical", "country"
        };
        Spinner genres = searchView.findViewById(R.id.genreDropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(searchView.getContext(),
                android.R.layout.simple_spinner_item, genreStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genres.setAdapter(adapter);
        genres.setSelection(0);

        // Set up tempo slider and toggle
        SeekBar tempoSlider = searchView.findViewById(R.id.tempoSlider);
        tempoSlider.setMax(250);
        tempoSlider.setEnabled(false);
        Switch tempoSwitch = searchView.findViewById(R.id.tempoSwitch);
        tempoSwitch.setOnCheckedChangeListener((view, checked) -> tempoSlider.setEnabled(checked));

        // Set up popularity slider and toggle
        SeekBar popSlider = searchView.findViewById(R.id.popularitySlider);
        popSlider.setMax(100);
        popSlider.setEnabled(false);
        Switch popSwitch = searchView.findViewById(R.id.popSwitch);
        popSwitch.setOnCheckedChangeListener((view, checked) -> popSlider.setEnabled(checked));

        // Set up text entry
        EditText artists = searchView.findViewById(R.id.artistNames);
        EditText tracks = searchView.findViewById(R.id.trackNames);

        viewModel = new ViewModelProvider(requireActivity()).get(SearchItemViewModel.class);

        // Set up search button
        final Button searchButton = searchView.findViewById(R.id.recommendation_button);
        searchButton.setOnClickListener(c -> {
            // TODO: More input validation
            Editable artistText = artists.getText();
            Editable trackText = tracks.getText();

            boolean failure = false;
            if (artistText == null || artistText.toString().isEmpty()) {
                failure = true;
                artists.setError("Please add an artist");
            }

            if (trackText == null || trackText.toString().isEmpty()) {
                failure = true;
                tracks.setError("Please add a track");
            }

            if (failure) {
                return;
            }

            List<String> artistLookup = Arrays.asList(artistText.toString().split(",\\s+"));
            List<String> trackLookup = Arrays.asList(trackText.toString().split(",\\s+"));

            // TODO: Graceful handling of too many inputs
            if (artistLookup.size() + trackLookup.size() > 4) {
                // Too many inputs. Drop until we're down to the limit
                int numArtists = Math.min((int) (4 * ((double)artistLookup.size() / (trackLookup.size() + artistLookup.size()))), 3);
                artistLookup = artistLookup.subList(0, numArtists);
                trackLookup = trackLookup.subList(0, 4 - numArtists);
            }

            viewModel.selectItem(new SearchItem(
                    artistLookup,
                    trackLookup,
                    new LinkedList<String>() {{  add(genres.getSelectedItem().toString()); }},
                    (tempoSlider.isEnabled()) ? tempoSlider.getProgress() : 0,
                    (popSlider.isEnabled()) ? popSlider.getProgress() : 0
            ));

            // Dismiss the keyboard
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tracks.getWindowToken(), 0);
        });

        return searchView;
    }
}