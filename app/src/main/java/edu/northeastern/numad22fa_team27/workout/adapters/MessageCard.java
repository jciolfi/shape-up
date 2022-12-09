package edu.northeastern.numad22fa_team27.workout.adapters;

/**
 * is a message card for the recycler view.
 * so that a user can view the title and a blurb
 * of the upcoming chat.
 */
public class MessageCard {
    private final String title;
    private final String body;

    public MessageCard(String title, String blurb) {
        this.title = title;
        this.body = blurb;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
