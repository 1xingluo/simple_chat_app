package com.example.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnReset;
    private RelativeLayout loadingOverlay;
    private ImageView bgImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset);
        loadingOverlay = findViewById(R.id.loading_overlay);
        bgImage = findViewById(R.id.bg);

        // 设置背景透明度 50%
        bgImage.setAlpha(0.5f);

        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 显示覆盖层
            loadingOverlay.setVisibility(View.VISIBLE);

            // 模拟网络延迟 2 秒
            new Handler().postDelayed(() -> {
                loadingOverlay.setVisibility(View.GONE);

                if (username.equals("admin") && password.equals("123456")) {
                    Toast.makeText(MainActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }, 2000);
        });

        btnReset.setOnClickListener(view -> {
            etUsername.setText("");
            etPassword.setText("");
            Toast.makeText(MainActivity.this, "已清空输入内容", Toast.LENGTH_SHORT).show();
        });
    }
}
