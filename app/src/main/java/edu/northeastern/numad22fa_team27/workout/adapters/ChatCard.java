package edu.northeastern.numad22fa_team27.workout.adapters;

/**
 * this is a chat card similar to the message card
 * this is for the use of the Read Message Activity
 * where the body of the text is needed.
 */
public class ChatCard {
    private final String body;
    private String userName;

    public ChatCard(String userName, String message) {
        this.userName = userName;
        this.body = message;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String username) {
        this.userName = username;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "ChatCard{" +
                "body='" + body + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
