package edu.northeastern.numad22fa_team27.workout.utilities;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.workout.adapters.AsyncChatAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.ChatCard;
import edu.northeastern.numad22fa_team27.workout.models.DAO.ChatDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.Message;
import edu.northeastern.numad22fa_team27.workout.models.User;

public class ChatUtil {

    private static final String TAG = "ChatUtil";
    private FirebaseAuth user_auth;
    private FirebaseFirestore db;
    private Message currChatData;
    private ListenerRegistration chatListener;
    private RecyclerView rvToUpdate;
    private AsyncChatAdapter asyncAdapter;

    public ChatUtil(Message originalMessages, AsyncChatAdapter asyncAdapter, RecyclerView rvToUpdate) {
        this.db = FirebaseFirestore.getInstance();
        this.user_auth = FirebaseAuth.getInstance();
        this.asyncAdapter = asyncAdapter;
        this.rvToUpdate = rvToUpdate;
        this.currChatData = originalMessages;
    }

    public void shutdown() {
        this.chatListener.remove();
    }

    public void watchConversationChanges(Map<String, String> usernameTranslationMap) {
        // Update on changes
        this.chatListener = db.collection(Constants.MESSAGES)
                .document(currChatData.getChatId())
                .addSnapshotListener((EventListener<DocumentSnapshot>) (snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        synchronized(currChatData) {
                            Message newMessages = new Message(snapshot.toObject(ChatDAO.class), user_auth.getUid());
                            if (currChatData.getChatHistory().size() == newMessages.getChatHistory().size()) {
                                // It's the same, no need to do anything
                                return;
                            }
                            currChatData = newMessages;
                        }

                        // Get the last field
                        Map<String, String> lastUpdate = currChatData.getChatHistory().get(currChatData.getChatHistory().size() - 1);
                        ChatCard newCard = new ChatCard(usernameTranslationMap.get(lastUpdate.get("userId")), lastUpdate.get("message"));
                        asyncAdapter.setCardAtPosition(asyncAdapter.getItemCount(), newCard);
                        asyncAdapter.notifyDataSetChanged();
                        rvToUpdate.scrollToPosition(asyncAdapter.getItemCount() - 1);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
    }

    public void ayncFindUsernameFromId(Map<String, String> usernameTranslationMap, String unknownId) {
        db.collection(Constants.USERS)
                .document(unknownId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    UserDAO ud = snapshot.toObject(UserDAO.class);
                    usernameTranslationMap.put(unknownId, ud.username);

                    for (int i = 0; i < asyncAdapter.getCards().size(); i++) {
                        ChatCard c = asyncAdapter.getCards().get(i);
                        Log.v("XYZ", "Looking at card " + c.toString());
                        if (c.getUserName() != null && c.getUserName().equals(unknownId)){
                            c.setUserName(usernameTranslationMap.get(unknownId));
                            asyncAdapter.setCardAtPosition(i, c);
                        }
                    }
                    asyncAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> {

                });
    }
}
