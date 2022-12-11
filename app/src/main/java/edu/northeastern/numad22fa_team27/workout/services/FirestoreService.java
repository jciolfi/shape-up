package edu.northeastern.numad22fa_team27.workout.services;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.callbacks.WorkoutCallback;
import edu.northeastern.numad22fa_team27.workout.models.DAO.ChatDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.Group;
import edu.northeastern.numad22fa_team27.workout.models.Message;
import edu.northeastern.numad22fa_team27.workout.models.WorkoutCategory;
import static edu.northeastern.numad22fa_team27.Constants.*;

public class FirestoreService implements IFirestoreService {
    private final String TAG = "FirestoreService";
    private final FirebaseFirestore firestoreDB;
    private final FirebaseAuth userAuth;
    private UserDAO currentUser;

    public FirestoreService() {
        firestoreDB = FirebaseFirestore.getInstance();
        userAuth = FirebaseAuth.getInstance();
        tryFetchUserDetails();
    }

    @Override
    public boolean tryCreateGroup(String groupName, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(groupName)) {
            warnBadParam("tryCreateGroup");
            return false;
        } else if (!tryFetchUserDetails()) {
            return false;
        } else if (currentUser.joinedGroups.size() >= 10) {
            // can't have more than 10 groups
            return false;
        }

        AtomicBoolean success = new AtomicBoolean(true);

        // we already check if we can get the current user from tryFetchUserDetails, so it shouldn't be null
        String userID = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        Group newGroup = new Group(groupName, userID);
        Message m = new Message(groupName);
        m.addChatMembers(userID);
        newGroup.setGroupChatId(m.getChatId());
        Log.v("XYZ", "Group to store " + newGroup.toString());
        Log.v("XYZ", "Message to store " + m.toString());

        firestoreDB.collection(GROUPS)
                .document(newGroup.getGroupID())
                .set(new GroupDAO(newGroup))
                .addOnSuccessListener(unused1 -> {

                    // Add ourselves to the group
                    firestoreDB.collection(USERS)
                            .document(userID)
                            .update(JOINED_GROUPS, FieldValue.arrayUnion(newGroup.getGroupID()))
                            .addOnSuccessListener(unused2 -> {
                                // overkill maybe, but query db to make sure it's actually in there
                                firestoreDB.collection(GROUPS)
                                        .document(newGroup.getGroupID())
                                        .get()
                                        .addOnSuccessListener(callback::processDocument)
                                        .addOnFailureListener(e -> {
                                            success.set(false);
                                            logFailure("tryCreateGroup", e.getMessage());
                                        });
                            })
                            .addOnFailureListener(e -> {
                                success.set(false);
                                logFailure("tryCreateGroup", e.getMessage());
                            });

                    // Create an associated chat group, with us in it
                    firestoreDB.collection(MESSAGES)
                            .document(m.getChatId())
                            .set(new ChatDAO(m))
                            .addOnFailureListener(e -> {
                                success.set(false);
                                logFailure("tryCreateGroup", e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    success.set(false);
                    logFailure("tryCreateGroup", e.getMessage());
                });

        return success.get();
    }

    @Override
    public boolean tryChangeGroupPrivacy(String groupID, boolean isPublic) {
        if (Util.stringIsNullOrEmpty(groupID)) {
            warnBadParam("tryChangeGroupPrivacy");
            return false;
        } else if (!tryFetchUserDetails()) {
            return false;
        }

        AtomicBoolean success = new AtomicBoolean(true);

        firestoreDB.collection(GROUPS)
                .document(groupID)
                .update(ACCEPTING_MEMBERS, isPublic)
                .addOnFailureListener(e -> {
                    success.set(false);
                    logFailure("tryChangeGroupPrivacy", e.getMessage());
                });

        return success.get();
    }

    @Override
    public void findWorkoutsByCriteria(String workoutName, WorkoutCategory workoutCategory, double maxDifficulty, double minDifficulty, WorkoutCallback callback, int resultLimit, boolean reverseOrder) {
        Query collectionQuery = firestoreDB.collection(WORKOUTS);
        boolean didFiltering = false;

        if (!Util.stringIsNullOrEmpty(workoutName)) {
            // Add workout name filtering
            didFiltering = true;
            collectionQuery = collectionQuery
                    .orderBy(WORKOUT_NAME, reverseOrder ? Query.Direction.DESCENDING : Query.Direction.ASCENDING)
                    .startAt(reverseOrder ? workoutName + "\uf8ff" : workoutName)
                    .endAt(reverseOrder ? workoutName : workoutName + "\uf8ff");
        }
        if (workoutCategory != null) {
            // Add workout category filtering
            didFiltering = true;
            collectionQuery = collectionQuery
                    .whereArrayContains("categoriesPresent", String.valueOf(workoutCategory));
        }

        if (maxDifficulty > -1) {
            // Add workout category filtering
            didFiltering = true;
            collectionQuery = collectionQuery
                    .whereLessThan(DIFFICULTY, String.valueOf(workoutCategory));
        }

        if (minDifficulty > -1) {
            // Add workout category filtering
            didFiltering = true;
            collectionQuery = collectionQuery
                    .whereGreaterThan(DIFFICULTY, String.valueOf(workoutCategory));
        }

        if (resultLimit > 0) {
            didFiltering = true;
            collectionQuery = collectionQuery.limit(resultLimit);
        }

        // We got to the end and they didn't give us anything to filter on
        if (!didFiltering) {
            warnBadParam("findWorkoutsByCriteria");
        }

        // In all cases, process the query and hand off successful results to our callback
        collectionQuery.get()
                .addOnSuccessListener(callback::processQuery)
                .addOnFailureListener(e -> logFailure("findWorkoutsByCriteria", e.getMessage()));
    }

    @Override
    public void findUsersByUsername(String username, WorkoutCallback callback, boolean reverseOrder) {
        if (Util.stringIsNullOrEmpty(username)) {
            warnBadParam("findUserByUsername");
            return;
        }

        firestoreDB.collection(USERS)
                .orderBy(USERNAME, reverseOrder ? Query.Direction.DESCENDING : Query.Direction.ASCENDING)
                .startAt(reverseOrder ? username + "\uf8ff" : username)
                .endAt(reverseOrder ? username : username + "\uf8ff")
                .get()
                .addOnSuccessListener(callback::processQuery)
                .addOnFailureListener(e -> logFailure("findUserByUsername", e.getMessage()));
    }

    @Override
    public void findUserGroups(WorkoutCallback callback) {
        //  for some reason, this breaks everything

        String userID = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();

        firestoreDB.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    UserDAO user = documentSnapshot.toObject(UserDAO.class);
                    if (user == null) {
                        logFailure("findUserGroups", String.format("%s doesn't exist", userID));
                        return;
                    }

                    if (user.joinedGroups.isEmpty()) {
                        Log.v(TAG, "User is not part of any groups");
                        return;
                    }

                    // get groups
                    firestoreDB.collection(GROUPS)
                            .whereIn(FieldPath.documentId(), user.joinedGroups)
                            .get()
                            .addOnSuccessListener(callback::processQuery)
                            .addOnFailureListener(e -> logFailure("findUserGroups", e.getMessage()));
                })
                .addOnFailureListener(e -> logFailure("findUserGroups", e.getMessage()));
    }

    @Override
    public void findGroupsByName(String groupName, WorkoutCallback callback, boolean reverseOrder) {
        if (Util.stringIsNullOrEmpty(groupName)) {
            warnBadParam("findGroupsByName");
            return;
        }

        firestoreDB.collection(GROUPS)
                .orderBy("groupName", reverseOrder ? Query.Direction.DESCENDING : Query.Direction.ASCENDING)
                .startAt(reverseOrder ? groupName + "\uf8ff" : groupName)
                .endAt(reverseOrder ? groupName : groupName + "\uf8ff")
                .get()
                .addOnSuccessListener(callback::processQuery)
                .addOnFailureListener(e -> logFailure("findGroupsByName", e.getMessage()));
    }

    @Override
    public void findStreaksLeaderboard(WorkoutCategory category, WorkoutCallback callback) {
        if (category == null) {
            warnBadParam("findStreaksLeaderboard");
            return;
        }

        firestoreDB.collection(USERS)
                .orderBy(String.format("bestCategoryStreaks.%s", category), Query.Direction.DESCENDING)
                .limit(100L)
                .get()
                .addOnSuccessListener(callback::processQuery)
                .addOnFailureListener(e -> logFailure("findStreaksLeaderboard", e.getMessage()));
    }

    @Override
    public void getGroupByID(String groupID, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(groupID)) {
            warnBadParam("getGroupByID");
            return;
        }

        firestoreDB.collection(GROUPS)
                .document(groupID)
                .get()
                .addOnSuccessListener(callback::processDocument)
                .addOnFailureListener(e -> logFailure("findGroupByID", e.getMessage()));
    }

    @Override
    public boolean tryJoinGroup(String groupID) {
        if (Util.stringIsNullOrEmpty(groupID)) {
            warnBadParam("tryJoinGroup");
            return false;
        } else if (!tryFetchUserDetails()) {
            warnBadParam("tryFetchUserDetails");
            return false;
        }else if (currentUser.joinedGroups.size() >= 10) {
            // not in same line as above to avoid race condition with getting currentUser
            return false;
        }

        String userID = userAuth.getCurrentUser().getUid();
        AtomicBoolean success = new AtomicBoolean(true);

        firestoreDB.collection(GROUPS)
                .document(groupID)
                .get()
                .addOnSuccessListener(snapshot -> {
                    GroupDAO groupDAO = snapshot.toObject(GroupDAO.class);
                    if (groupDAO == null || !groupDAO.acceptingMembers) {
                        success.set(false);
                    } else {
                        // add user to group
                        firestoreDB.collection(GROUPS)
                                .document(groupID)
                                .update(MEMBERS, FieldValue.arrayUnion(userID))
                                .addOnFailureListener(e -> {
                                    // hit here if group doesn't exist
                                    success.set(false);
                                    logFailure("tryJoinGroup",
                                            String.join("%s doesn't exist: %s", groupID, e.getMessage()));
                                })
                                .addOnSuccessListener(unused -> {
                                    // add group to user
                                    firestoreDB.collection(USERS)
                                            .document(userID)
                                            .update(JOINED_GROUPS, FieldValue.arrayUnion(groupID))
                                            .addOnFailureListener(e -> {
                                                success.set(false);
                                                logFailure("tryJoinGroup", e.getMessage());
                                            });

                                    // Add group to to the user's list of chats
                                    firestoreDB.collection(USERS)
                                            .document(userID)
                                            .update("chats", FieldValue.arrayUnion(groupDAO.groupChatId))
                                            .addOnFailureListener(e -> {
                                                success.set(false);
                                                logFailure("tryJoinGroup", e.getMessage());
                                            });

                                    // Add user to the list of chatters
                                    firestoreDB.collection(GROUPS)
                                            .document(groupDAO.groupChatId)
                                            .update("members", FieldValue.arrayUnion(userID))
                                            .addOnFailureListener(e -> {
                                                success.set(false);
                                                logFailure("tryJoinGroup", e.getMessage());
                                            });
                                });
                    }
                });

        return success.get();
    }

    @Override
    public void leaveGroup(String groupID) {
        if (Util.stringIsNullOrEmpty(groupID) || !tryFetchUserDetails()) {
            warnBadParam("leaveGroup");
            return;
        }

        String userID = userAuth.getCurrentUser().getUid();

        firestoreDB.collection(GROUPS)
                .document(groupID)
                .get()
                .addOnSuccessListener(snapshot -> {
                    // remove this user from group if not admin
                    GroupDAO groupDAO = snapshot.toObject(GroupDAO.class);
                    if (groupDAO != null && !groupDAO.adminID.equals(userID)) {
                        firestoreDB.collection(GROUPS)
                                .document(groupID)
                                .update(MEMBERS, FieldValue.arrayRemove(userID))
                                .addOnFailureListener(e -> logFailure("leaveGroup", e.getMessage()));

                        // remove group from this user
                        firestoreDB.collection(USERS)
                                .document(userID)
                                .update(JOINED_GROUPS, FieldValue.arrayRemove(groupID))
                                .addOnFailureListener(e -> logFailure("leaveGroup", e.getMessage()));

                        // remove group chat from the user's list of chats
                        firestoreDB.collection(USERS)
                                .document(userID)
                                .update("chats", FieldValue.arrayRemove(groupDAO.groupChatId))
                                .addOnFailureListener(e -> logFailure("leaveGroup", e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> logFailure("leaveGroup", e.getMessage()));
    }

    @Override
    public void getUserByID(String userID, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(userID)) {
            warnBadParam("getUserByID");
            return;
        }

        firestoreDB.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener(callback::processDocument)
                .addOnFailureListener(e -> logFailure("getUserByID", e.getMessage()));
    }

    @Override
    public boolean tryRequestFriend(String friendID) {
        if (Util.stringIsNullOrEmpty(friendID) || !tryFetchUserDetails()) {
            warnBadParam("tryRequestFriend");
            return false;
        }

        String userID = userAuth.getCurrentUser().getUid();

        // can't request self as friend
        if (userID.equals(friendID)) {
            return false;
        }

        AtomicBoolean success = new AtomicBoolean(true);

        firestoreDB.collection(USERS)
                .document(friendID)
                .get()
                .addOnFailureListener(e -> {
                    success.set(false);
                    logFailure("tryRequestFriend", e.getMessage());
                })
                .addOnSuccessListener(snapshot -> {
                    UserDAO requestedFriend = snapshot.toObject(UserDAO.class);

                    // can't request user that doesn't exist/they're already friends with this user
                    // the check for the requestedFriend's friend list is the important part here
                    if (requestedFriend == null || requestedFriend.friends.contains(userID)) {
                        success.set(false);
                    } else {
                        // add friend request
                        firestoreDB.collection(USERS)
                                .document(friendID)
                                .update("incomingFriendRequests", FieldValue.arrayUnion(userID))
                                .addOnFailureListener(e -> {
                                    success.set(false);
                                    logFailure("tryRequestFriend", e.getMessage());
                                });
                    }
                });

        return success.get();
    }

    @Override
    public boolean tryAcceptFriendRequest(String friendID) {
        if (Util.stringIsNullOrEmpty(friendID) || !tryFetchUserDetails()) {
            warnBadParam("tryAcceptFriendRequest");
            return false;
        }

        String userID = userAuth.getCurrentUser().getUid();

        // can't friend self
        if (userID.equals(friendID)) {
            return false;
        }

        AtomicBoolean success = new AtomicBoolean(true);

        firestoreDB.collection(USERS)
                .document(friendID)
                .get()
                .addOnFailureListener(e -> {
                    success.set(false);
                    logFailure("tryAcceptFriendRequest", e.getMessage());
                })
                .addOnSuccessListener(snapshot -> {
                    // query for requestedFriend see if they exist and get their friend list
                    UserDAO userWhoSentRequest = snapshot.toObject(UserDAO.class);

                    if (userWhoSentRequest == null) {
                        // if user doesn't exist with friendID, can't accept user as friend
                        success.set(false);
                    } else if (!currentUser.incomingFriendRequests.contains(friendID)) {
                        // with no request - success depends on if both already friends
                        success.set(currentUser.friends.contains(friendID) && userWhoSentRequest.friends.contains(userID));
                    } else {
                        // handle when this user has request and user who sent request exists

                        // remove userWhoSentRequest from requests, add to this user's frends list
                        firestoreDB.collection(USERS)
                                .document(userID)
                                .update(
                                        "incomingFriendRequests", FieldValue.arrayRemove(friendID),
                                        "friends", FieldValue.arrayUnion(friendID)
                                )
                                .addOnFailureListener(e -> {
                                    success.set(false);
                                    logFailure("tryAcceptFriendRequest", e.getMessage());
                                })
                                .addOnSuccessListener(unused -> {
                                    // add this user to userWhoSentRequest's friends
                                    firestoreDB.collection(USERS)
                                            .document(friendID)
                                            .update("friends", FieldValue.arrayUnion(userID))
                                            .addOnFailureListener(e -> {
                                                success.set(false);
                                                logFailure("tryAcceptFriendRequest", e.getMessage());
                                            });
                                });
                    }
                });

        return success.get();
    }

    @Override
    public boolean tryRejectFriendRequest(String friendID) {
        if (Util.stringIsNullOrEmpty(friendID) || !tryFetchUserDetails()) {
            warnBadParam("tryRejectFriendRequest");
            return false;
        }

        String userID = userAuth.getCurrentUser().getUid();
        AtomicBoolean success = new AtomicBoolean(true);

        if (currentUser.incomingFriendRequests.contains(friendID)) {
            firestoreDB.collection(USERS)
                    .document(userID)
                    .update("incomingFriendRequests", FieldValue.arrayRemove(friendID))
                    .addOnFailureListener(e -> {
                        success.set(false);
                        logFailure("tryRejectFriendRequest", e.getMessage());
                    });
        }

        return success.get();
    }

    @Override
    public void removeFriend(String friendID) {
        if (Util.stringIsNullOrEmpty(friendID) || !tryFetchUserDetails()) {
            warnBadParam("removeFriend");
            return;
        }

        String userID = userAuth.getCurrentUser().getUid();

        // we don't really care if these users don't exist, then they're already removed
        // remove friend from this user
        firestoreDB.collection(USERS)
                .document(userID)
                .update(FRIENDS, FieldValue.arrayRemove(friendID))
                .addOnFailureListener(e -> logFailure("tryRemoveFriend", e.getMessage()));

        // remove this user from friend
        firestoreDB.collection(USERS)
                .document(friendID)
                .update(FRIENDS, FieldValue.arrayRemove(userID))
                .addOnFailureListener(e -> logFailure("tryRemoveFriend", e.getMessage()));
    }

    @Override
    public void johntest() {
        firestoreDB.collection(USERS)
                .document("RR5HSyOk2Zbhh6p7ZtnxNbSjeeC3")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Log.d("JOHNTEST", snapshot.toString());
                })
                .addOnFailureListener(e -> {
                    Log.d("JOHNTEST", e.toString());
                });
    }

    // ---------- Helpers ----------
    private boolean tryFetchUserDetails() {
        FirebaseUser user = userAuth.getCurrentUser();
        if (user == null) {
            Log.w(TAG, "Could not fetch current user");
            return false;
        }

        String userID = user.getUid();

        AtomicBoolean success = new AtomicBoolean(true);
        Task<DocumentSnapshot> getter = firestoreDB.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "hit success!" + documentSnapshot.toObject(UserDAO.class));
                    currentUser = documentSnapshot.toObject(UserDAO.class);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "hit fail!");
                    success.set(false);
                    logFailure("tryFetchUserDetails", e.getMessage());
                });

        Log.d(TAG, "returning: " + success.get() + " and " + (currentUser != null));
        return success.get() && currentUser != null;
    }

    private void warnBadParam(String methodName) {
        Log.w(TAG, String.format("%s: fields were null", methodName));
    }

    private void logFailure(String methodName, String message) {
        Log.w(TAG, String.format("%s Failure: %s", methodName, message));
    }
}
