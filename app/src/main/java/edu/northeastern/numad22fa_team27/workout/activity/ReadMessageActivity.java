package edu.northeastern.numad22fa_team27.workout.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.northeastern.numad22fa_team27.R;

/**
 * displays the chat history of a single chat. all viewers
 * should be able to view the same chat.
 */
public class ReadMessageActivity extends AppCompatActivity {

    private String chatId;
    private  String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_meassage);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            chatId = extras.getString("chatId");

            title = extras.getString("title");
        }
    }
}