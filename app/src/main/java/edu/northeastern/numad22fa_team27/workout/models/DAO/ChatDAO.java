package edu.northeastern.numad22fa_team27.workout.models.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.models.Message;

public class ChatDAO {
    public String chatId;
    public String title;
    public List<String> members;
    public List<Map<String,String>> messages;

    public ChatDAO(String chatId, String title, List<String> members, List<Map<String, String>> messages) {
        this.chatId = chatId;
        this.title = title;
        this.members = members;
        this.messages = messages;
    }

    public ChatDAO() {
        this.chatId = "";
        this.title = "";
        this.members = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public ChatDAO(Message m) {
        this.chatId = m.getChatId();
        this.title = m.getName();
        this.members = m.getChatMembers();
        this.messages = m.getChatHistory();
    }

    @Override
    public String toString() {
        return "ChatDAO{" +
                "chatId='" + chatId + '\'' +
                ", title='" + title + '\'' +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
