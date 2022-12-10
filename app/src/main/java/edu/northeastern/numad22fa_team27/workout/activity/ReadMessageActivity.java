package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import edu.northeastern.numad22fa_team27.Constants;
import edu.northeastern.numad22fa_team27.R;
import edu.northeastern.numad22fa_team27.workout.adapters.AsyncChatAdapter;
import edu.northeastern.numad22fa_team27.workout.adapters.ChatCard;
import edu.northeastern.numad22fa_team27.workout.models.DAO.ChatDAO;
import edu.northeastern.numad22fa_team27.workout.models.DAO.UserDAO;
import edu.northeastern.numad22fa_team27.workout.models.Message;
import edu.northeastern.numad22fa_team27.workout.utilities.ChatUtil;

/**
 * displays the chat history of a single chat. all viewers
 * should be able to view the same chat.
 */
public class ReadMessageActivity extends AppCompatActivity {

    private String chatId;
    private RecyclerView recMessages;
    private ViewPager2 vpMessages;
    private EditText editText;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    FirebaseFirestore firestore;
    private AsyncChatAdapter adapter;
    private Map<String, String> idToUsernameMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_meassage);
        firestore = FirebaseFirestore.getInstance();
        AtomicReference<Message> currMessages = new AtomicReference(new Message());
        idToUsernameMap = new HashMap<>();

        progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            chatId = extras.getString("chatId");
        }
        //chat id

        //initialize recycler
        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        adapter = new AsyncChatAdapter();
        recMessages = findViewById(R.id.rcv_message_view);
        recMessages.setHasFixedSize(false);
        recMessages.setAdapter(adapter);
        recMessages.setLayoutManager(manager);

        //initialize Edit text
        editText = findViewById(R.id.txt_edit_message);

        //initialize send message
        sendButton = findViewById(R.id.btn_send_message);
        sendButton.setOnClickListener(V -> {
            //query database and make changes

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentID = user.getUid();
            String newMessage = editText.getText().toString();
            if (newMessage.equals("")){
                editText.setError("Please add a message");
                return;
            }

            // If we add the update to the message object directly, or differential callback won't think there has been a change.
            ChatDAO cd = new ChatDAO(currMessages.get());
            cd.messages = new ArrayList<>();
            cd.messages.addAll(currMessages.get().getChatHistory());
            cd.messages.add(new HashMap<>() {{ put("userId", currentID); put("message", newMessage); }});
            firestore.collection(Constants.MESSAGES)
                    .document(chatId)
                    .set(cd)
                    .addOnSuccessListener(unused -> {
                        editText.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(findViewById(R.id.txt_edit_message),InputMethodManager.SHOW_IMPLICIT);
                    }).addOnFailureListener(e -> {
                        editText.setText("");
                        editText.setError("Could not send!");
                        return;
                    });
        });

        // Initial retrieval of data
        firestore.collection(Constants.MESSAGES)
                .document(chatId)
                .get()
                .addOnSuccessListener(ds -> {
                    ChatDAO cd = ds.toObject(ChatDAO.class);
                    currMessages.set(new Message(chatId, cd.title, cd.members, cd.messages));
                    Log.v("XYZ", currMessages.toString());

                    // Update the RecyclerView
                    for (int i = 0; i < currMessages.get().getChatHistory().size(); i++) {
                        Map<String, String> map = currMessages.get().getChatHistory().get(i);
                        ((AsyncChatAdapter) recMessages.getAdapter()).setCardAtPosition(i, new ChatCard(map.get("userId"), map.get("message")));
                    }
                    recMessages.getAdapter().notifyDataSetChanged();
                    recMessages.scrollToPosition(adapter.getItemCount() - 1);
                    progressBar.setVisibility(View.INVISIBLE);

                    // Watch for changes in the future
                    ChatUtil watcher = new ChatUtil(currMessages.get(), adapter, recMessages);
                    watcher.watchConversationChanges(idToUsernameMap);

                    // Now, find out who these users are
                    for (String m : currMessages.get().getChatMembers()) {
                        watcher.ayncFindUsernameFromId(idToUsernameMap, m);
                    }
                });
    }

}