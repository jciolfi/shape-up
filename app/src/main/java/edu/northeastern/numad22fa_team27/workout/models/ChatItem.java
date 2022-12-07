package edu.northeastern.numad22fa_team27.workout.models;

public class ChatItem {

    private final String[] users;

    public ChatItem(String[] users) {
        this.users = users;
    }

    public String[] getUsers(){
        return users;
    }
}
