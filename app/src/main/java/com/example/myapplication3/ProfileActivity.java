package com.example.myapplication3;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);

        // 设置默认头像
        ivAvatar.setImageResource(R.drawable.photo1);

        // 获取当前登录用户名
        String username = getIntent().getStringExtra("username");
        tvUsername.setText(username != null ? username : "未登录");
    }
}
