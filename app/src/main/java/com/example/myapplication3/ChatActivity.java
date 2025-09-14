package com.example.myapplication3;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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

        // 获取传参
        myId = getIntent().getIntExtra("myId", -1);
        friendId = getIntent().getIntExtra("friendId", -1);

        listView = findViewById(R.id.listView);
        inputMsg = findViewById(R.id.inputMsg);
        btnSend = findViewById(R.id.btnSend);

        // 初始化适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        // 发送按钮
        btnSend.setOnClickListener(v -> {
            String text = inputMsg.getText().toString().trim();
            if (!text.isEmpty()) {
                dbHelper.sendMessage(myId, friendId, text);
                inputMsg.setText("");
                loadMessages();
            }
        });

        // 定时刷新聊天记录（每2秒刷新一次）
        refreshTask = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(refreshTask);
    }

    /** 加载消息并显示用户名 + 内容 + 时间 */
    private void loadMessages() {
        List<DBHelper.MessageItem> messages = dbHelper.getMessagesWithUsername(myId, friendId);
        List<String> displayList = new ArrayList<>();
        for (DBHelper.MessageItem item : messages) {
            String prefix = (item.senderName.equals(dbHelper.getUsername(myId))) ? "我: " : item.senderName + ": ";
            displayList.add(prefix + item.content + " (" + item.timestamp + ")");
        }
        adapter.clear();
        adapter.addAll(displayList);
        adapter.notifyDataSetChanged();

        // 滚动到底部
        if(displayList.size() > 0){
            listView.smoothScrollToPosition(displayList.size() - 1);
        }

        // 标记对方消息为已读
        dbHelper.markMessagesAsRead(myId, friendId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshTask);
    }
}
