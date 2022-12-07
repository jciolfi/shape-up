package edu.northeastern.numad22fa_team27.workout.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.workout.callbacks.WorkoutCallback;
import edu.northeastern.numad22fa_team27.workout.models.DAO.GroupDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.Group;
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
    public void createGroup(String groupName) {
        if (Util.stringIsNullOrEmpty(groupName)) {
            warnBadParam("createGroup");
            return;
        } else if (!tryFetchUserDetails()) {
            return;
        }

        // we already check if we can get the current user from tryFetchUserDetails, so it shouldn't be null
        String userID = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        Group newGroup = new Group(groupName, userID);

        firestoreDB.collection(GROUPS)
                .document(String.valueOf(newGroup.getGroupID()))
                .set(new GroupDAO(newGroup))
                .addOnSuccessListener(unused1 -> {
                    this.currentUser.joinedGroups.add(String.valueOf(newGroup.getGroupID()));
                    firestoreDB.collection(USERS)
                            .document(userID)
                            .update("joinedGroups", this.currentUser.joinedGroups)
                            .addOnSuccessListener(unused2 -> {
                                // TODO
                                Log.d(TAG, String.format("Successfully created group %s", groupName));
                            })
                            .addOnFailureListener(e -> logFailure("createGroup", e.getMessage()));

                })
                .addOnFailureListener(e -> logFailure("createGroup", e.getMessage()));
    }

    @Override
    public void findWorkoutsByCriteria(String workoutName, WorkoutCategory workoutCategory, double maxDifficulty, double minDifficulty, WorkoutCallback callback, int resultLimit) {
        Query collectionQuery = firestoreDB.collection(WORKOUTS);
        boolean didFiltering = false;

        if (!Util.stringIsNullOrEmpty(workoutName)) {
            // Add workout name filtering
            didFiltering = true;
            collectionQuery = collectionQuery
                    .orderBy(WORKOUT_NAME)
                    .startAt(workoutName)
                    .endAt(workoutName + "\uf8ff");

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
    public void findUsersByUsername(String username, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(username)) {
            warnBadParam("findUserByUsername");
            return;
        }

        firestoreDB.collection(USERS)
                .orderBy(USERNAME)
                .startAt(username)
                .endAt(username + "\uf8ff")
                .get()
                .addOnSuccessListener(callback::processQuery)
                .addOnFailureListener(e -> logFailure("findUserByUsername", e.getMessage()));
    }

    @Override
    public void findUserGroups(WorkoutCallback callback) {
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
    public void findGroupsByName(String groupName, WorkoutCallback callback) {
        if (Util.stringIsNullOrEmpty(groupName)) {
            warnBadParam("findGroupsByName");
            return;
        }

        firestoreDB.collection(GROUPS)
                .orderBy("groupName")
                .startAt(groupName)
                .endAt(groupName + "\uf8ff")
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
    public boolean tryJoinGroup(String groupID) {
        if (Util.stringIsNullOrEmpty(groupID) || !tryFetchUserDetails()) {
            warnBadParam("joinGroup");
            return false;
        } else if (currentUser.joinedGroups.size() >= 10) {
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
                    if (groupDAO == null) {
                        success.set(false);
                    } else {
                        // add user to group
                        Group group = new Group(groupDAO, snapshot.getId());

                        if (group.getMembers().contains(userID)) {
                            success.set(false);
                        } else {
                            groupDAO.members.add(userID);
                            firestoreDB.collection(GROUPS)
                                    .document(groupID)
                                    .update("members", groupDAO.members)
                                    .addOnSuccessListener(unused -> {
                                        // add group to user
                                        currentUser.joinedGroups.add(groupID);
                                        firestoreDB.collection(USERS)
                                                .document(userID)
                                                .set(currentUser)
                                                .addOnFailureListener(e -> {
                                                    success.set(false);
                                                    logFailure("tryJoinGroup", e.getMessage());
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        success.set(false);
                                        logFailure("tryJoinGroup", e.getMessage());
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    success.set(false);
                    logFailure("tryJoinGroup", e.getMessage());
                });

        return success.get();
    }

    @Override
    public boolean tryLeaveGroup(String groupID) {
        if (Util.stringIsNullOrEmpty(groupID) || !tryFetchUserDetails()) {
            warnBadParam("joinGroup");
            return false;
        }

        String userID = userAuth.getCurrentUser().getUid();
        AtomicBoolean success = new AtomicBoolean(true);

        firestoreDB.collection(GROUPS)
                .document(groupID)
                .get()
                .addOnSuccessListener(snapshot -> {
                    GroupDAO groupDAO = snapshot.toObject(GroupDAO.class);
                    if (groupDAO == null) {
                        success.set(false);
                    } else {
                        // remove user from group
                        groupDAO.members.remove(userID);
                        firestoreDB.collection(GROUPS)
                                .document(groupID)
                                .update("members", groupDAO.members)
                                .addOnSuccessListener(unused -> {
                                    // remove group from user
                                    currentUser.joinedGroups.remove(groupID);
                                    firestoreDB.collection(USERS)
                                            .document(userID)
                                            .update("joinedGroups", currentUser.joinedGroups)
                                            .addOnFailureListener(e -> {
                                                success.set(false);
                                                logFailure("tryJoinGroup", e.getMessage());
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    success.set(false);
                                    logFailure("tryJoinGroup", e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    success.set(false);
                    logFailure("tryJoinGroup", e.getMessage());
                });

        return success.get();
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
                .addOnFailureListener(e -> logFailure("tryJoinGroup", e.getMessage()));
    }

    @Override
    public boolean tryRequestFriend(String friendID) {
        if (Util.stringIsNullOrEmpty(friendID) || !tryFetchUserDetails()) {
            warnBadParam("joinGroup");
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

                    // can't request someone you're already friends with
                    if (requestedFriend == null || requestedFriend.friends.contains(userID)) {
                        success.set(false);
                    } else {
                        // if this user already requested the friend, that's fine. Still return true
                        if (!requestedFriend.incomingFriendRequests.contains(userID)) {
                            firestoreDB.collection(USERS)
                                    .document(friendID)
                                    .update("incomingFriendRequests", FieldValue.arrayUnion(userID))
                                    .addOnFailureListener(e -> {
                                        success.set(false);
                                        logFailure("tryRequestFriend", e.getMessage());
                                    });
                        }
                    }
                });

        return success.get();
    }

    @Override
    public boolean tryAcceptFriendRequest(String friendID) {
        if (Util.stringIsNullOrEmpty(friendID) || !tryFetchUserDetails()) {
            warnBadParam("joinGroup");
            return false;
        }

        String userID = userAuth.getCurrentUser().getUid();
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
                        // if no friend found, can't accept friend
                        success.set(false);
                    } else if (!currentUser.incomingFriendRequests.contains(friendID)) {
                        // success depends on if they're both already friends when no friend request present
                        success.set(currentUser.friends.contains(friendID) && userWhoSentRequest.friends.contains(userID));
                    } else {
                        // hit here only when this user has an incoming friend request from given friendID

                        // remove friend from requests, add friend to user
                        if (!currentUser.friends.contains(friendID)) {
                            currentUser.friends.add(friendID);
                        }

                        // happens outside of if since requests need to always be removed
                        firestoreDB.collection(USERS)
                                .document(userID)
                                .update(
                                        "incomingFriendRequests", FieldValue.arrayRemove(friendID),
                                        "friends", currentUser.friends
                                )
                                .addOnFailureListener(e -> {
                                    success.set(false);
                                    logFailure("tryAcceptFriendRequest", e.getMessage());
                                });

                        // add user to friend if not already friends
                        if (success.get() && !userWhoSentRequest.friends.contains(userID)) {
                            firestoreDB.collection(USERS)
                                    .document(friendID)
                                    .update("friends", FieldValue.arrayUnion(userID))
                                    .addOnFailureListener(e -> {
                                        success.set(false);
                                        logFailure("tryAcceptFriendRequest", e.getMessage());
                                    });
                        }
                    }
                });

        return success.get();
    }

    @Override
    public boolean tryRemoveFriend(String friendID) {
        if (Util.stringIsNullOrEmpty(friendID) || !tryFetchUserDetails()) {
            warnBadParam("joinGroup");
            return false;
        }

        String userID = userAuth.getCurrentUser().getUid();
        AtomicBoolean success = new AtomicBoolean(true);

        firestoreDB.collection(USERS)
                .document(friendID)
                .get()
                .addOnFailureListener(e -> {
                    success.set(false);
                    logFailure("tryAcceptFriend", e.getMessage());
                })
                .addOnSuccessListener(snapshot -> {
                    UserDAO friendToRemove = snapshot.toObject(UserDAO.class);

                    if (friendToRemove == null) {
                        success.set(false);
                    } else {
                        // if they're not friends to begin with, still don't care - could still return true
                        // remove friend from user
                        if (currentUser.friends.contains(friendID)) {
                            firestoreDB.collection(USERS)
                                    .document(userID)
                                    .update(FRIENDS, FieldValue.arrayRemove(friendID))
                                    .addOnFailureListener(e -> {
                                        success.set(false);
                                        logFailure("tryAcceptFriend", e.getMessage());
                                    });
                        }

                        // remove user from friend
                        if (success.get() && friendToRemove.friends.contains(userID)) {
                            firestoreDB.collection(USERS)
                                    .document(friendID)
                                    .update(FRIENDS, FieldValue.arrayRemove(userID))
                                    .addOnFailureListener(e -> {
                                        success.set(false);
                                        logFailure("tryAcceptFriend", e.getMessage());
                                    });
                        }
                    }
                });

        return success.get();
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
        firestoreDB.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentUser = documentSnapshot.toObject(UserDAO.class);
                })
                .addOnFailureListener(e -> success.set(false));

        return success.get() && currentUser != null;
    }

    private void warnBadParam(String methodName) {
        Log.w(TAG, String.format("%s: fields were null", methodName));
    }

    private void logFailure(String methodName, String message) {
        Log.w(TAG, String.format("%s Failure: %s", methodName, message));
    }
}
