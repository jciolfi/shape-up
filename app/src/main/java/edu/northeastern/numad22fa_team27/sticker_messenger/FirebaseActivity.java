package edu.northeastern.numad22fa_team27.sticker_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.Util;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.UserDAO;

public class FirebaseActivity extends AppCompatActivity {
    private final String TAG = FirebaseActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /**
         * TODO:
         * [x] login flow (login or signup with username)
         * [] add a friend (do we need a notion of a friend request?)
         * [] incoming message listener (show notification)
         * [] button to send a sticker
         */
        promptLogin();
    }

    /**
     * Create an AlertDialog with field for users to enter username
     */
    private void promptLogin() {
        final EditText usernameText = new EditText(this);
        AlertDialog loginDialog = new AlertDialog.Builder(this)
                .setTitle("Log in with your username")
                .setMessage("If you don't have an account, one will be made for you")
                .setView(usernameText)
                .setPositiveButton("Log in", null)
                .setCancelable(false)
                .create();

        // add listener for "Log in" button - leave dialog up if username unspecified
        loginDialog.setOnShowListener(dialogInterface -> {
            Button loginButton = loginDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            String enteredUser = usernameText.getText().toString();
            loginButton.setOnClickListener(view -> {
                if (Util.stringIsNullOrEmpty(enteredUser)) {
                    usernameText.setError("Username can't be empty");
                } else {
                    username = enteredUser;
                    insertUserIfNotExists(username);
                    loginDialog.dismiss();
                }
            });
        });
        loginDialog.show();
    }

    /**
     * Add an entry for the given username if it doesn't already exist
     * @param user the username to (potentially) insert
     */
    private void insertUserIfNotExists(String user) {
        Query usernameQuery = mDatabase.child("users").child(user);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    UserDAO newUser = new UserDAO(user);
                    mDatabase.child("users").child(user).setValue(newUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}