package com.example.myapplication3;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private EditText inputMsg;
    private Button btnSend;
    private ArrayAdapter<String> adapter;

    private DBHelper dbHelper;
    private int myId;       // 当前用户ID
    private int friendId;   // 对方ID
    private String myName;  // 当前用户名
    private String friendName; // 对方用户名

    private Handler handler = new Handler();
    private Runnable refreshTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = findViewById(R.id.listView);
        inputMsg = findViewById(R.id.inputMsg);
        btnSend = findViewById(R.id.btnSend);

        dbHelper = new DBHelper(this);

        // 从 Intent 获取用户 ID
        myId = getIntent().getIntExtra("myId", -1);
        friendId = getIntent().getIntExtra("friendId", -1);

        // 获取用户名
        myName = dbHelper.getUsername(myId);
        friendName = dbHelper.getUsername(friendId);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        loadMessages();

        btnSend.setOnClickListener(v -> {
            String text = inputMsg.getText().toString().trim();
            if (!text.isEmpty()) {
                dbHelper.sendMessage(myId, friendId, text);
                inputMsg.setText("");
                loadMessages();
                listView.smoothScrollToPosition(adapter.getCount() - 1);
            }
        });

        // 定时刷新聊天记录
        refreshTask = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(refreshTask);
    }

    private void loadMessages() {
        List<DBHelper.MessageItem> messages = dbHelper.getMessagesWithUsername(myId, friendId);

        List<String> displayList = new ArrayList<>();
        for (DBHelper.MessageItem item : messages) {
            displayList.add(item.senderName + ": " + item.content + " (" + item.timestamp + ")");
        }

        adapter.clear();
        adapter.addAll(displayList);
        adapter.notifyDataSetChanged();

        dbHelper.markMessagesAsRead(myId, friendId);

        if (!displayList.isEmpty()) {
            listView.smoothScrollToPosition(displayList.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshTask);
    }
}
