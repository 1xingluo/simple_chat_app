package com.example.myapplication3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private ImageView ivAvatar;
    private DBHelper dbHelper;
    private CommonLayoutView commonLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 使用你上面的 ConstraintLayout 布局

        // 初始化控件
        ivAvatar = findViewById(R.id.iv_avatar);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        commonLayoutView = findViewById(R.id.common_layout_view);

        dbHelper = new DBHelper(this);

        // 用户名输入变化时更新头像
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                loadAvatar(s.toString().trim());
            }
        });

        // 登录按钮点击
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()){
                commonLayoutView.showToast("请输入用户名和密码");
                return;
            }

            // 显示加载中
            commonLayoutView.showLoading(true);

            // 模拟网络延迟，实际可以放后台线程或异步请求
            etUsername.postDelayed(() -> {
                if(dbHelper.checkUser(username, password)){
                    commonLayoutView.showToast("登录成功");
                    loadAvatar(username);

                    // 跳转列表页面
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    commonLayoutView.showToast("用户名或密码错误");
                }

                // 隐藏加载中
                commonLayoutView.showLoading(false);
            }, 1000); // 延迟 1 秒，模拟加载
        });

        // 注册按钮点击
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /** 加载用户头像 */
    private void loadAvatar(String username){
        if(username.isEmpty()){
            ivAvatar.setImageResource(R.drawable.photo1);
            return;
        }
        File file = new File(getFilesDir(), username + "_avatar.png");
        if(file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivAvatar.setImageBitmap(bitmap);
        } else {
            ivAvatar.setImageResource(R.drawable.photo1);
        }
    }
}
