package edu.northeastern.numad22fa_team27;

import static androidx.core.content.ContextCompat.getSystemService;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private View searchView;
    private SearchItemViewModel viewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchView = inflater.inflate(R.layout.fragment_search, container, false);

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

            if (artistText == null || artistText.toString().isEmpty()) {
                failure = true;
                tracks.setError("Please add a track");
            }

            if (failure) {
                return;
            }

            List<String> artistLookup = Arrays.asList(artistText.toString().split(",\\s+"));
            List<String> trackLookup = Arrays.asList(trackText.toString().split(",\\s+"));

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