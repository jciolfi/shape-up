package edu.northeastern.numad22fa_team27.workout.models;

import java.util.Objects;

public class MediaParagraph {
    private String paragraphText;
    private String mediaURL;

    public MediaParagraph(String paragraphText, String mediaURL) {
        this.paragraphText = paragraphText;
        this.mediaURL = mediaURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaParagraph that = (MediaParagraph) o;
        return paragraphText.equals(that.paragraphText) && mediaURL.equals(that.mediaURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paragraphText, mediaURL);
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public void setParagraphText(String paragraphText) {
        this.paragraphText = paragraphText;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }
}
