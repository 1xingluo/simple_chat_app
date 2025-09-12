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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private ImageView bgImage, ivAvatar;
    private RelativeLayout loadingOverlay;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        ivAvatar = findViewById(R.id.iv_avatar);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        bgImage = findViewById(R.id.bg);
        loadingOverlay = findViewById(R.id.loading_overlay);

        bgImage.setAlpha(0.5f);
        dbHelper = new DBHelper(this);

        // 用户名输入变化时更新头像
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString().trim();
                loadAvatar(username);
            }
        });

        // 登录按钮
        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
                return;
            }

            loadingOverlay.setVisibility(RelativeLayout.VISIBLE);

            if(dbHelper.checkUser(username,password)){
                Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
                loadAvatar(username);

                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Toast.makeText(this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
            }

            loadingOverlay.setVisibility(RelativeLayout.GONE);
        });

        // 注册按钮
        btnRegister.setOnClickListener(view -> {
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
            ivAvatar.setImageResource(R.drawable.photo1); // 默认头像
        }
    }
}
