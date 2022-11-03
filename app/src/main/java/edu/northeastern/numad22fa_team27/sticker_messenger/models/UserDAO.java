package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDAO {
    // unique username for this User
    public String username;
    // collection of friends identified by their usernames
    public Set<String> friends;
    // collection of stickers this user has received
    public List<IncomingMessage> incomingMessages;
    // collection of stickers this user has sent
    public List<OutgoingMessage> outgoingMessages;

    public UserDAO(String username) {
        this.username = username;
        this.friends = new HashSet<>();
        this.incomingMessages = new ArrayList<>();
        this.outgoingMessages = new ArrayList<>();
    }

    public UserDAO(String username,
                   Set<String> friends,
                   List<IncomingMessage> incomingMessages,
                   List<OutgoingMessage> outgoingMessages) {
        this.username = username;
        this.friends = friends;
        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
    }
}
