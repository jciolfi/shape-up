package edu.northeastern.numad22fa_team27.workout.models.DAO;

import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_team27.workout.models.Message;

public class MessageDOA {
    public List<String> members;
    public List<Map<String,String>> messages;
    public String title;

    public MessageDOA() {}

    public MessageDOA(Message m) {
        this.title = m.getName();
        this.messages = m.getChatHistory();
        this.members = m.getChatMembers();

    }

    @Override
    public String toString() {
        return "MessageDOA{" +
                "title=" + this.title + '\'' +
                ", members=" + members +
                ", messages=" + messages + '\'' +
                "}";
    }
}
