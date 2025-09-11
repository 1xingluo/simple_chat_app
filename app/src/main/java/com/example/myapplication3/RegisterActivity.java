package com.example.myapplication3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnRegister;
    private ImageView bgImage;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        bgImage = findViewById(R.id.bg);

        bgImage.setAlpha(0.5f);
        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
                return;
            }
            if(dbHelper.isUserExist(username)){
                Toast.makeText(this,"用户名已存在",Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = dbHelper.addUser(username,password);
            if(success){
                Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
