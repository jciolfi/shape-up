package edu.northeastern.numad22fa_team27.workout.models.DAO;

import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.Message;

public class ChatDAO {
    public String chatId;
    public String title;
    public List<String> members;
    public List<Map<String,String>> messages;

    public ChatDAO() {

    }

    public ChatDAO(Message m) {

    }
}
