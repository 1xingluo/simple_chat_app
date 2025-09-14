package com.example.myapplication3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private EditText inputMsg;
    private Button btnSend;

    private DBHelper dbHelper;
    private ChatAdapter adapter;
    private List<DBHelper.MessageItem> messages;

    private int currentUserId;
    private String currentUsername;
    private int friendId;
    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = findViewById(R.id.listView);
        inputMsg = findViewById(R.id.inputMsg);
        btnSend = findViewById(R.id.btnSend);

        dbHelper = new DBHelper(this);

        // 获取Intent数据
        currentUserId = getIntent().getIntExtra("currentUserId", -1);
        currentUsername = getIntent().getStringExtra("currentUsername");
        friendId = getIntent().getIntExtra("friendId", -1);
        friendName = getIntent().getStringExtra("friendName");

        // 获取消息
        messages = dbHelper.getMessagesWithUsername(currentUserId, friendId);
        adapter = new ChatAdapter(this, messages, currentUsername);
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String text = inputMsg.getText().toString().trim();
            if(!text.isEmpty()){
                dbHelper.sendMessage(currentUserId, friendId, text);
                inputMsg.setText("");
                loadMessages();
            }
        });

        // 定时刷新聊天记录，每2秒刷新
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMessages();
                listView.postDelayed(this, 2000);
            }
        }, 2000);
    }

    private void loadMessages(){
        messages.clear();
        messages.addAll(dbHelper.getMessagesWithUsername(currentUserId, friendId));
        adapter.notifyDataSetChanged();
        if(messages.size() > 0){
            listView.smoothScrollToPosition(messages.size() - 1);
        }

        // 标记对方消息为已读
        dbHelper.markMessagesAsRead(currentUserId, friendId);
    }
}
