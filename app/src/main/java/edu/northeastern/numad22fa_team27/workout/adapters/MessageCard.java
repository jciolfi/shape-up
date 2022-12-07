package edu.northeastern.numad22fa_team27.workout.adapters;

public class MessageCard {
    private final String title;
    private final String lastMessage;

    public MessageCard(String title, String blurb) {
        this.title = title;
        this.lastMessage = blurb;
    }

    public String getTitle() {
        return title;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
