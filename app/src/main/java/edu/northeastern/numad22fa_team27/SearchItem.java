package edu.northeastern.numad22fa_team27;

import java.util.List;

public class SearchItem {

    public SearchItem(List<String> artistNames, List<String> trackNames, List<String> genres, int tempo, int popularity) {
        this.artistNames = artistNames;
        this.trackNames = trackNames;
        this.genres = genres;
        this.tempo = tempo;
        this.popularity = popularity;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public List<String> getTrackNames() {
        return trackNames;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getTempo() {
        return tempo;
    }

    public int getPopularity() {
        return popularity;
    }

    private List<String> artistNames;
    private List<String> trackNames;
    private List<String> genres;
    private int tempo;
    private int popularity;
}
