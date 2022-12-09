package edu.northeastern.numad22fa_team27.workout.interfaces;

public interface Summarizeable {
    /**
     * Provide a title for this entry that the user can read
     * @return title
     */
    String getTitle();

    /**
     * Any kind of description text
     * @return misc text
     */
    String getMisc();

    /**
     * URL for image to load
     * @return image url
     */
    String getImage();
}
