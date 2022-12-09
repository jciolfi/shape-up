package edu.northeastern.numad22fa_team27.workout.adapters;

/**
 * this is a chat card similar to the message card
 * this is for the use of the Read Message Activity
 * where the body of the text is needed.
 */
public class ChatCard {
    private final String userName;
    private final String body;

    public ChatCard(String userName, String message) {
        this.userName = userName;
        this.body = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getBody() {
        return body;
    }
}
