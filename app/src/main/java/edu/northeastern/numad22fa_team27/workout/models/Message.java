package edu.northeastern.numad22fa_team27.workout.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Message {
    private String chatID;
    private String name;

    public String getLastMessage() {
        return lastMessage;
    }

    private String lastMessage;
    private List<String> chatMembers;
    private List<String> chatHistory;

    public Message(String chatID) {
        this.chatID = chatID;
        this.chatMembers = new ArrayList<String>();
        this.chatHistory = new ArrayList<String>();
    }


    public Message(String chatID, List<String> chatMembers, List<String> chatHistory) {
        this.chatID = chatID;
        this.chatMembers = chatMembers;
        this.chatHistory = chatHistory;
        this.lastMessage = chatHistory.get(chatHistory.size() - 1);
    }


    public String getChatID() {
        return chatID;
    }

    public List<String> getChatMembers() {
        return chatMembers;
    }

    public List<String> getChatHistory() {
        return chatHistory;
    }

    public void setChatMembers(List<String> chatMembers) {
        this.chatMembers = chatMembers;
    }

    public void setChatHistory(List<String> chatHistory) {
        this.chatHistory = chatHistory;
        this.lastMessage = chatHistory.get(chatHistory.size() - 1);
    }

    public void addChatMembers(String member) {
        this.chatMembers.add(member);
    }

    public void addChatHistory(String message) {
        this.chatHistory.add(message);
        this.lastMessage = message;
    }
}
