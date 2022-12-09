package edu.northeastern.numad22fa_team27.workout.models;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_team27.workout.models.DAO.ChatDAO;

public class Message {
    private String chatId;
    private String name;
    private String lastMessage;
    private List<String> chatMembers;
    private List<Map<String, String>> chatHistory;

    public Message(String chatId, String name) {
        this.chatId = chatId;
        this.name = name;
        this.chatMembers = new ArrayList<String>();
        this.chatHistory = new ArrayList<>();
    }

    /**
     * this is a constructor for a new message that is usually
     * created by the Message Fragment completion.
     * initializes the first message to the user that created it and
     * the sentece "Created a chat called Title"
     * @param chatId the id of the chat begins unknown
     * @param name the name of the chat
     * @param chatMembers the chat members.
     */
    public Message(String chatId, String name, List<String> chatMembers) {
        this.chatId = chatId;
        this.name = name;
        this.chatMembers = chatMembers;
        this.chatHistory = new ArrayList<>();
        //added new chat history from the creator
        addChatHistory(chatMembers.get(0), "GroupChat " + name + " was created");
        this.lastMessage = chatHistory.get(chatHistory.size() - 1).get("message");

    }


    /**
     * constructor for a card
     * @param chatId the id of the chat
     * @param name the name of the chat
     * @param chatMembers the chat members (id)
     * @param chatHistory the chat history list of map
     */
    public Message(String chatId, String name, List<String> chatMembers, List<Map<String, String>> chatHistory) {
        this.chatId = chatId;
        this.name = name;
        this.chatMembers = chatMembers;
        this.chatHistory = new ArrayList<>();
        if (!chatHistory.isEmpty()) {
            this.lastMessage = chatHistory.get(chatHistory.size() - 1).get("message");
        } else {
            this.lastMessage = "lastMessage"; //chatHistory.get(chatHistory.size() - 1);
        }
    }
    public Message(ChatDAO mDOA, String chatId){
        this.chatId = chatId;
        this.name = mDOA.title;
        this.chatHistory = mDOA.messages;
        this.chatMembers = mDOA.members;
        if (!chatHistory.isEmpty()) {
            this.lastMessage = chatHistory.get(chatHistory.size() - 1).get("message");
        } else {
            this.lastMessage = "lastMessage"; //chatHistory.get(chatHistory.size() - 1);
        }

    }


    public String getChatId() {
        return chatId;
    }
    public String getName() {return name;}

    public List<String> getChatMembers() {
        return chatMembers;
    }

    public List<Map<String, String>> getChatHistory() {
        return chatHistory;
    }
    public String getLastMessage() {
        return lastMessage;
    }

    public void setChatMembers(List<String> chatMembers) {
        this.chatMembers = chatMembers;
    }

    public void setChatHistory(List<Map<String, String>> chatHistory) {
        this.chatHistory = chatHistory;
        //this.lastMessage = chatHistory.get(chatHistory.size() - 1);
    }

    public void addChatMembers(String member) {
        this.chatMembers.add(member);
    }

    public void addChatHistory(String userId, String message) {
        Map<String, String> newMessage = new ArrayMap<>();
        newMessage.put("userId", userId);
        newMessage.put("message", message);
        chatHistory.add(newMessage);
        this.lastMessage = chatHistory.get(chatHistory.size() - 1).get("message");
    }
}
