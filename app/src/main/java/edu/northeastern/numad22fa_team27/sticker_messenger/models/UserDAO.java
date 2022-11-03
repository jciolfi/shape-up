package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // unique username for this User
    public String username;
    // collection of friends identified by their usernames
    public List<String> friends;
    // collection of stickers this user has received
    public List<IncomingMessage> incomingMessages;
    // collection of stickers this user has sent
    public List<OutgoingMessage> outgoingMessages;

    public UserDAO(String username) {
        this.username = username;
        this.friends = new ArrayList<>();
        this.incomingMessages = new ArrayList<>();
        this.outgoingMessages = new ArrayList<>();
    }
}
