package edu.northeastern.numad22fa_team27.spotify.spotifyviews;

import android.graphics.drawable.Icon;

public class Cards {
    private final Icon artistImage;
    private final String artistName;
    private final String trackName;

    public Cards(Icon artistImage, String artistName, String trackName) {
        this.artistImage = artistImage;
        this.artistName = artistName;
        this.trackName = trackName;
    }

    public String getArtistName() {

        return artistName;
    }

    public String getTrackName() {

        return trackName;
    }

    public Icon getArtistImage() {

        return artistImage;
    }
}