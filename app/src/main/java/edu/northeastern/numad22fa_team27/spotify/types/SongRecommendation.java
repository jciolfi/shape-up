package edu.northeastern.numad22fa_team27.spotify.types;

import androidx.annotation.NonNull;

public class SongRecommendation {
    private String trackName;
    private String artistName;
    private String imageLarge;
    private String imageMedium;
    private String imageSmall;

    public SongRecommendation(String trackName, String artistName, String imageLarge, String imageMedium, String imageSmall) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.imageLarge = imageLarge;
        this.imageMedium = imageMedium;
        this.imageSmall = imageSmall;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getImageLarge() {
        return imageLarge;
    }

    public String getImageMedium() {
        return imageMedium;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    @NonNull
    @Override
    public String toString() {
        return "SongRecommendation{" +
                "trackName='" + trackName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", imageLarge='" + imageLarge + '\'' +
                ", imageMedium='" + imageMedium + '\'' +
                ", imageSmall='" + imageSmall + '\'' +
                '}';
    }
}
