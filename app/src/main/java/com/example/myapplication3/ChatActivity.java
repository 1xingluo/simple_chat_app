package com.example.myapplication3;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
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

    private Handler handler = new Handler();
    private Runnable refreshTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHelper = new DBHelper(this);

        // 从 Intent 获取传参
        myId = getIntent().getIntExtra("myId", -1);
        friendId = getIntent().getIntExtra("friendId", -1);

        listView = findViewById(R.id.listView);
        inputMsg = findViewById(R.id.inputMsg);
        btnSend = findViewById(R.id.btnSend);

        loadMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputMsg.getText().toString().trim();
                if (!text.isEmpty()) {
                    dbHelper.sendMessage(myId, friendId, text);
                    inputMsg.setText("");
                    loadMessages();
                }
            }
        });

        // 定时刷新聊天记录（每 2 秒刷新一次）
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
        List<String> messages = dbHelper.getMessages(myId, friendId);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(adapter);

        // 标记对方发来的消息为已读
        dbHelper.markMessagesAsRead(myId, friendId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshTask);
    }
}
