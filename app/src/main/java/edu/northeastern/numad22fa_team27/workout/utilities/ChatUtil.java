package edu.northeastern.numad22fa_team27.workout.utilities;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.workout.models.DAO.ChatDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.Message;
import edu.northeastern.numad22fa_team27.workout.models.User;

public class ChatUtil {

    private static final String TAG = "ChatUtil";
    private static ChatUtil INSTANCE;
    private FirebaseAuth user_auth;
    private FirebaseFirestore db;
    private Message currChatData;
    private ListenerRegistration chatListener;

    private ChatUtil() {
        db = FirebaseFirestore.getInstance();
        user_auth = FirebaseAuth.getInstance();
        currChatData = new Message();
    }

    public static ChatUtil getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ChatUtil();
        }
        return INSTANCE;
    }

    public void startWatchingChatChanges(String chatId) {
        // Update on changes
        ListenerRegistration chatListener = db.collection(Constants.MESSAGES)
                .document(chatId)
                .addSnapshotListener((EventListener<DocumentSnapshot>) (snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        synchronized(currChatData) {
                            currChatData = new Message(snapshot.toObject(ChatDAO.class), user_auth.getUid());
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
    }

    public Message getUser() {
        return currChatData;
    }

    public void updateUser(Message m) {
        synchronized (currChatData) {
            currChatData = m;
            db.collection(Constants.USERS)
                    .document(user_auth.getUid())
                    .set(new ChatDAO(m));
        }
    }
}
