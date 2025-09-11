package com.example.myapplication3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddFriendActivity extends AppCompatActivity {

    private EditText etUsername;
    private Button btnSearch, btnAdd;
    private TextView tvResult;
    private DBHelper dbHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        dbHelper = new DBHelper(this);
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        etUsername = findViewById(R.id.et_username);
        btnSearch = findViewById(R.id.btn_search);
        btnAdd = findViewById(R.id.btn_add);
        tvResult = findViewById(R.id.tv_result);

        btnSearch.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            int friendId = dbHelper.getUserId(username);
            if (friendId == -1) {
                tvResult.setText("用户不存在");
                btnAdd.setEnabled(false);
            } else if (friendId == currentUserId) {
                tvResult.setText("不能添加自己");
                btnAdd.setEnabled(false);
            } else {
                tvResult.setText("找到用户: " + username);
                btnAdd.setEnabled(true);
                btnAdd.setOnClickListener(view -> {
                    if (dbHelper.addFriendRequest(friendId, currentUserId)) {
                        Toast.makeText(this, "好友请求已发送", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "请求已存在或已是好友", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
