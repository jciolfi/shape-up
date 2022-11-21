package edu.northeastern.numad22fa_team27.workout.models;

public class MediaParagraph {
    private String paragraphText;
    private String mediaURL;

    public MediaParagraph(String paragraphText, String mediaURL) {
        this.paragraphText = paragraphText;
        this.mediaURL = mediaURL;
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
