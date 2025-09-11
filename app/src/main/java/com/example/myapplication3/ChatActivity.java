package com.example.myapplication3;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView lvChat;
    private EditText etInput;
    private Button btnSend, btnBack;
    private TextView tvTitle;

    private List<ChatMessage> messageList = new ArrayList<>();
    private ChatAdapter adapter;

    private String contactName;  // 好友名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        lvChat = findViewById(R.id.lv_chat);
        etInput = findViewById(R.id.et_input);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);

        // 从上一个界面接收好友名字
        contactName = getIntent().getStringExtra("name");
        if(contactName == null || contactName.isEmpty()){
            contactName = "好友聊天"; // 默认名字
        }
        tvTitle.setText(contactName);

        btnBack.setOnClickListener(v -> finish());

        adapter = new ChatAdapter(this, messageList);
        lvChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String text = etInput.getText().toString().trim();
            if(!text.isEmpty()){
                // 自己消息
                ChatMessage msg = new ChatMessage(text, ChatMessage.TYPE_SENT);
                messageList.add(msg);
                adapter.notifyDataSetChanged();
                lvChat.setSelection(messageList.size() - 1);
                etInput.setText("");

                // 模拟对方回复
                new Handler().postDelayed(() -> {
                    ChatMessage reply = new ChatMessage("收到：" + text, ChatMessage.TYPE_RECEIVED);
                    messageList.add(reply);
                    adapter.notifyDataSetChanged();
                    lvChat.setSelection(messageList.size() - 1);
                }, 1000);
            }
        });
    }
}