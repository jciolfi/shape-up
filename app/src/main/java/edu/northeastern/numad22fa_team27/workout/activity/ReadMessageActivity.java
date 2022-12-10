package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.ChatAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.ChatCard;
import edu.northeastern.numad22fa_team27.workout.adapters.MessageAdapter;
import edu.northeastern.numad22fa_team27.workout.models.Message;

/**
 * displays the chat history of a single chat. all viewers
 * should be able to view the same chat.
 */
public class ReadMessageActivity extends AppCompatActivity {

    private String chatId;
    private String title;
    private ArrayList<String> chatMembers;
    private List<String> userNames;
    private Map<String, String> users;
    private List<Map<String, String>> chatHistory;
    private RecyclerView recMessages;
    private EditText editText;
    private ImageButton sendButton;
    private List<ChatCard> cards;
    private ProgressBar progressBar;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_meassage);

        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            chatId = extras.getString("chatId");

            title = extras.getString("title");

            chatMembers = extras.getStringArrayList("chatMembers");
        }
        cards = new ArrayList<>();
        //chat id

        //initialize recycler
        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recMessages = findViewById(R.id.rcv_message_view);
        recMessages.setHasFixedSize(false);
        recMessages.setAdapter(new ChatAdapter(cards));
        recMessages.setLayoutManager(manager);

        //initialize Edit text
        editText = findViewById(R.id.txt_edit_message);

        //initialize send message
        sendButton = findViewById(R.id.btn_send_message);
        sendButton.setOnClickListener(V -> {
            //querry database and make changes

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentID = user.getUid();
            String newMessage = editText.getText().toString();
            if (newMessage.equals("")){
                editText.setError("please add a message");
                return;
            }
            Map<String, String> addMap = new HashMap<>();
            addMap.put("userId", currentID);
            addMap.put("message", newMessage);
            chatHistory.add(addMap);
            Map<String, Object> newInput = new HashMap<>();
            newInput.put("messages", chatHistory);
            firestore.collection("messages")
                    .document(chatId.trim())
                    .set(newInput, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            cards.add(new ChatCard(users.get(currentID),  newMessage));
                            recMessages.getAdapter().notifyDataSetChanged();
                            editText.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput( findViewById(R.id.txt_edit_message),InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

        });
        firestore = FirebaseFirestore.getInstance();

        //find the data for the message
        users = new HashMap<>();
        for (String s : chatMembers) {
            firestore.collection("users")
                    .document(s.trim())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String username = "";
                            try {
                                username = (String) documentSnapshot
                                        .getData()
                                        .get("username");
                            } catch (NullPointerException e) {
                                //don't need to do anything will create a new chat
                            }
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            users.put(s.trim(), username);
                        }
                    });
        }


        firestore.collection("messages")
                .document(chatId.trim())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        chatHistory = new ArrayList<>();
                        try {
                            chatHistory = (List<Map<String, String>>) documentSnapshot
                                    .getData()
                                    .get("messages");
                        } catch (NullPointerException e) {
                            //don't need to do anything will create a new chat
                        }
                        for (Map<String, String> map: chatHistory) {
                            cards.add(new ChatCard(users.get(map.get("userId")), map.get("message")));
                        }
                        recMessages.getAdapter().notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

    }
}