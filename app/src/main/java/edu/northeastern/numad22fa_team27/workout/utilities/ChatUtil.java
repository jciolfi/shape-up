package edu.northeastern.numad22fa_team27.workout.utilities;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.workout.adapters.ChatCard;
import edu.northeastern.numad22fa_team27.workout.models.DAO.ChatDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.Message;

public class ChatUtil {

    private static final String TAG = "ChatUtil";
    private FirebaseFirestore db;
    private AtomicReference<Message> currChatData;
    private ListenerRegistration chatListener;
    private RecyclerView rvToUpdate;
    private List<ChatCard> rvCards;

    public ChatUtil(AtomicReference<Message> originalMessages, List<ChatCard> rvCards, RecyclerView rvToUpdate) {
        this.db = FirebaseFirestore.getInstance();
        this.rvCards = rvCards;
        this.rvToUpdate = rvToUpdate;
        this.currChatData = originalMessages;
    }

    public void shutdown() {
        this.chatListener.remove();
    }

    public void watchConversationChanges(Map<String, String> usernameTranslationMap) {
        // Update on changes
        this.chatListener = db.collection(Constants.MESSAGES)
                .document(currChatData.get().getChatId())
                .addSnapshotListener((EventListener<DocumentSnapshot>) (snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshot == null || !snapshot.exists()) {
                        Log.d(TAG, "Current data: null");
                        return;
                    }

                    ChatDAO newChat = snapshot.toObject(ChatDAO.class);
                    Map<String, String> lookupMap = new HashMap<>();

                    // First, look up chat members
                    List<Task<DocumentSnapshot>> lookups = newChat.members.stream()
                            .map(friendId -> db.collection(Constants.USERS)
                                    .document(friendId)
                                    .get())
                            .collect(Collectors.toList());

                    Tasks.whenAll(lookups).addOnSuccessListener(v -> {
                        for (int i = 0; i < lookups.size(); i++) {
                            Task<DocumentSnapshot> completedTask = lookups.get(i);
                            DocumentSnapshot result = completedTask.getResult();
                            UserDAO ud = result.toObject(UserDAO.class);
                            if (ud == null) {
                                continue;
                            }
                            lookupMap.put(
                                    newChat.members.get(i),
                                    ud.username
                            );
                        }

                        // Now that we have a lookup table, display the chat
                        rvCards.clear();
                        for (Map<String, String> kv: newChat.messages) {
                            String username = (lookupMap.containsKey(kv.get("userId")))
                                    ? lookupMap.get(kv.get("userId"))
                                    : "???";
                            rvCards.add(new ChatCard(username, kv.get("message")));
                        }
                        currChatData.set(new Message(currChatData.get().getChatId(), newChat.title, newChat.members, newChat.messages));
                        rvToUpdate.getAdapter().notifyDataSetChanged();
                        rvToUpdate.scrollToPosition(rvToUpdate.getAdapter().getItemCount() - 1);
                    });
                });
    }

}
