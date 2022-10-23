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

    private final List<String> artistNames;
    private final List<String> trackNames;
    private final List<String> genres;
    private final int tempo;
    private final int popularity;
}
