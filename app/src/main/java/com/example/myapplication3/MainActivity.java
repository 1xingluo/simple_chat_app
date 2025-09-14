package com.example.myapplication3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private ImageView ivAvatar;
    private DBHelper dbHelper;
    private CommonLayoutView commonLayoutView;

    // ActivityResultLauncher 用于选择图片
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivAvatar = findViewById(R.id.iv_avatar);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        commonLayoutView = findViewById(R.id.common_layout_view);

        dbHelper = new DBHelper(this);

        // 注册图片选择回调
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        Uri uri = result.getData().getData();
                        if(uri != null){
                            saveAvatar(uri, etUsername.getText().toString().trim());
                        }
                    }
                }
        );

        // 点击头像换头像
        ivAvatar.setOnClickListener(v -> {
            if(etUsername.getText().toString().trim().isEmpty()){
                commonLayoutView.showToast("请先输入用户名");
                return;
            }
            // 打开图库选择图片
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

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

            commonLayoutView.showLoading(true);
            etUsername.postDelayed(() -> {
                if(dbHelper.checkUser(username, password)){
                    commonLayoutView.showToast("登录成功");
                    loadAvatar(username);

                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    commonLayoutView.showToast("用户名或密码错误");
                }
                commonLayoutView.showLoading(false);
            }, 1000);
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

    /** 保存头像到本地 */
    private void saveAvatar(Uri uri, String username){
        if(username.isEmpty()) return;
        try{
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            File file = new File(getFilesDir(), username + "_avatar.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            ivAvatar.setImageBitmap(bitmap);
            commonLayoutView.showToast("头像已保存");
        } catch (Exception e){
            e.printStackTrace();
            commonLayoutView.showToast("头像保存失败");
        }
    }
}
