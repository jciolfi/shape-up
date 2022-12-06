package edu.northeastern.numad22fa_team27.workout.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MediaParagraph implements Parcelable {
    private String paragraphText;
    private String mediaURL;

    public MediaParagraph() {}

    public MediaParagraph(String paragraphText, String mediaURL) {
        this.paragraphText = paragraphText;
        this.mediaURL = mediaURL;
    }

    protected MediaParagraph(Parcel in) {
        paragraphText = in.readString();
        mediaURL = in.readString();
    }

    public static final Creator<MediaParagraph> CREATOR = new Creator<MediaParagraph>() {
        @Override
        public MediaParagraph createFromParcel(Parcel in) {
            return new MediaParagraph(in);
        }

        @Override
        public MediaParagraph[] newArray(int size) {
            return new MediaParagraph[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(paragraphText);
        dest.writeString(mediaURL);
    }
}
