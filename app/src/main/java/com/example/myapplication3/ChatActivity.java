package com.example.myapplication3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatName;
    private Button btnBack;
    private ListView lvChat;
    private EditText etMessage;
    private Button btnSend;

    private List<ChatMessage> messageList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private String chatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 获取传递的联系人名字
        chatName = getIntent().getStringExtra("name");

        // 初始化控件
        tvChatName = findViewById(R.id.tv_chat_name);
        btnBack = findViewById(R.id.btn_back);
        lvChat = findViewById(R.id.lv_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        // 设置顶部显示联系人名字
        tvChatName.setText(chatName);

        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 设置适配器
        chatAdapter = new ChatAdapter(this, messageList);
        lvChat.setAdapter(chatAdapter);

        // 发送消息
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                ChatMessage sentMsg = new ChatMessage(text, ChatMessage.TYPE_SENT);
                messageList.add(sentMsg);
                chatAdapter.notifyDataSetChanged();
                lvChat.setSelection(messageList.size() - 1); // 消息自动上移
                etMessage.setText("");
            }
        });
    }
}
