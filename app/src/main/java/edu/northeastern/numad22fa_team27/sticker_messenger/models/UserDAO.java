package edu.northeastern.numad22fa_team27.sticker_messenger.models;

import java.util.List;

public class UserDAO {
    // unique username for this User
    public String username;
    // collection of friends identified by their usernames
    public List<String> friends;
}
