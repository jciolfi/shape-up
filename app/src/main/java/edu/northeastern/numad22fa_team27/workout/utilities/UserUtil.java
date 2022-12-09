package edu.northeastern.numad22fa_team27.workout.utilities;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.User;

public class UserUtil {
    private static final String TAG = "UserUtil";
    private static UserUtil INSTANCE;
    private FirebaseAuth user_auth;
    private FirebaseFirestore db;
    private User currUserData;
    private ListenerRegistration userListener;

    private UserUtil() {
        db = FirebaseFirestore.getInstance();
        user_auth = FirebaseAuth.getInstance();
        currUserData = new User();
    }

    public static UserUtil getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserUtil();
        }
        return INSTANCE;
    }

    public void startWatchingUserChanges() {
        // Update on changes
        userListener = db.collection(Constants.USERS)
                .document(user_auth.getUid())
                .addSnapshotListener((EventListener<DocumentSnapshot>) (snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        synchronized(currUserData) {
                            currUserData = new User(snapshot.toObject(UserDAO.class), user_auth.getUid());
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
    }

    public User getUser() {
        return currUserData;
    }

    public void updateUser(User u) {
        synchronized (currUserData) {
            currUserData = u;
            db.collection(Constants.USERS)
                    .document(user_auth.getUid())
                    .set(new UserDAO(u));
        }
    }
}
