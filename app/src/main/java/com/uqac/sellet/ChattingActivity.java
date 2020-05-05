package com.uqac.sellet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.uqac.sellet.entities.Chat;
import com.uqac.sellet.entities.Message;
import com.uqac.sellet.entities.OnReadyListener;
import com.uqac.sellet.entities.User;

import java.text.DateFormat;

public class ChattingActivity extends AppCompatActivity {
    private static final String TAG = "ChattingActivity";
    private static final int READY_TOTAL = 3;
    private FirebaseAuth mAuth;

    private LinearLayout listOfMessages;
    private EditText messageInput;

    private User me;
    private User him;

    private Chat chat;

    private int readyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        setContentView(R.layout.activity_chatting);
        listOfMessages = findViewById(R.id.messagesList);
        messageInput = findViewById(R.id.messageInput);

        readyCount = 0;

        me = new User(ChattingActivity.this, mAuth.getCurrentUser().getUid()).setOnReadyListener(new OnReadyListener<User>() {
            @Override
            public void onReady(User u) {
//                ChattingActivity.this.me = u;
                addReady();
            }
        });

        him = new User(ChattingActivity.this, getIntent().getStringExtra("himId")).setOnReadyListener(new OnReadyListener<User>() {
            @Override
            public void onReady(User u) {
//                ChattingActivity.this.him = u;
                addReady();
            }
        });

        chat = new Chat(getIntent().getStringExtra("himId")).setOnReadyListener(new OnReadyListener<Chat>() {
            @Override
            public void onReady(Chat c) {
                Log.d(TAG, c.messages.toString());
                addReady();
            }
        });
    }

    private void addReady(){
        readyCount++;
        Log.d(TAG, "readyCount: "+ Integer.toString(readyCount));
        if(readyCount == READY_TOTAL){
            for (Message msg : chat.messages){
                addMessageToMessageList(msg);
            }
            findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(v);
                }
            });
        }
    }

    private void addMessageToMessageList(Message msg){
        LinearLayout messageView = (LinearLayout) LayoutInflater.from(ChattingActivity.this).inflate(R.layout.message_bubble, listOfMessages, false);

        ((TextView)(messageView.findViewById(R.id.author))).setText(authorName(msg));
        ((TextView)(messageView.findViewById(R.id.timestamp))).setText(DateFormat.getDateTimeInstance().format(msg.timestamp.toDate()));
        TextView vcontent = (TextView)(messageView.findViewById(R.id.content));
        vcontent.setText(msg.content);

        if(isMessageByMe(msg)){
            vcontent.setGravity(Gravity.RIGHT);
        }else{
            vcontent.setGravity(Gravity.LEFT);
        }

        listOfMessages.addView(messageView);
    }

    private String authorName(Message msg){
        if(isMessageByMe(msg)){
            return me.name;
        }
        return him.name;
    }

    private boolean isMessageByMe(Message msg){
        return msg.author.equals(me.uid);
    }

    public void sendMessage(View view){
        chat.addMessage(messageInput.getText().toString())
                .setOnReadyListener(new OnReadyListener<Message>() {
                    @Override
                    public void onReady(Message msg) {
                        addMessageToMessageList(msg);
                    }
                });
        messageInput.setText("");
        messageInput.clearFocus();
        Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show();
    }
}