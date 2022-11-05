package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDAO {
    // unique username for this User
    public String username;
    // collection of friends identified by their usernames
    public List<String> friends;
    // collection of stickers this user has received
    public List<IncomingMessage> incomingMessages;
    // collection of stickers this user has sent
    public List<OutgoingMessage> outgoingMessages;

    public UserDAO() { }

    public UserDAO(String username) {
        this.username = username;
        this.friends = new ArrayList<>();
        this.incomingMessages = new ArrayList<>();
        this.outgoingMessages = new ArrayList<>();
    }

    public UserDAO(String username,
                   List<String> friends,
                   List<IncomingMessage> incomingMessages,
                   List<OutgoingMessage> outgoingMessages) {
        this.username = username;
        this.friends = friends;
        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
    }
}
