package com.example.myapplication3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class WhackAMoleActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView tvScore;
    private Button btnStart, btnPause;

    private Button[][] buttons = new Button[3][3];
    private int score = 0;
    private int moleRow = -1, moleCol = -1;
    private boolean moleVisible = false; // 当前地鼠是否存在
    private boolean gameOver = false;    // 游戏是否结束
    private boolean paused = false;      // 是否暂停
    private int highScore = 0;           // 用户的最高分数

    private Handler handler = new Handler();
    private Random random = new Random();
    private Runnable moleRunnable;
    private Runnable speedRunnable;

    private long moleInterval = 1000; // 初始刷新间隔（1000毫秒）
    private long speedIncreaseInterval = 5000; // 每5秒加快

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whack_a_mole);

        // 获取当前用户的唯一 ID
        String userId = getUserIdFromPreferences(); // 假设从登录系统中获取

        // 根据 userId 动态生成独立的 SharedPreferences 文件
        sharedPreferences = getSharedPreferences(userId + "_WhackAMolePreferences", MODE_PRIVATE);

        // 获取保存的最高分数
        highScore = sharedPreferences.getInt("highScore", 0);

        // 获取界面元素
        gridLayout = findViewById(R.id.grid_layout);
        tvScore = findViewById(R.id.tv_score);
        btnStart = findViewById(R.id.btn_start);
        btnPause = findViewById(R.id.btn_pause);

        // 设置按钮点击事件
        btnStart.setOnClickListener(v -> {
            clearAllMoles(); // 强制清空一次
            startGame();
        });

        btnPause.setOnClickListener(v -> togglePause());

        initBoard();
    }

    // 获取当前登录的用户ID
    private String getUserIdFromPreferences() {
        // 假设用户已经登录，ID从登录系统或 SharedPreferences 获取
        // 这里用一个假设的用户ID（你可以根据实际情况更改）
        return "user123"; // 根据实际情况替换
    }

    // 初始化网格
    private void initBoard() {
        gridLayout.removeAllViews();
        int numRows = 3, numCols = 3;
        int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Button btn = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);
                params.width = 0;
                params.height = 0;
                params.setMargins(marginPx, marginPx, marginPx, marginPx);
                btn.setLayoutParams(params);
                btn.setBackgroundColor(0xFFAAAAAA);
                btn.setText("");

                final int r = i, c = j;
                btn.setOnClickListener(v -> clickCell(r, c));

                gridLayout.addView(btn);
                buttons[i][j] = btn;
            }
        }
    }

    // 清除所有地鼠
    private void clearAllMoles() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackgroundColor(0xFFAAAAAA);
            }
        }
        moleRow = -1;
        moleCol = -1;
        moleVisible = false;
    }

    // 开始游戏
    private void startGame() {
        clearAllMoles();
        score = 0;
        tvScore.setText("分数: 0");
        moleVisible = false;
        moleRow = -1;
        moleCol = -1;
        gameOver = false;
        paused = false;
        btnPause.setText("暂停");

        if (moleRunnable != null) handler.removeCallbacks(moleRunnable);
        if (speedRunnable != null) handler.removeCallbacks(speedRunnable);

        moleRunnable = new Runnable() {
            @Override
            public void run() {
                if (!gameOver && !paused) showMole();
                if (!gameOver && !paused) handler.postDelayed(this, moleInterval);
            }
        };
        handler.post(moleRunnable);

        speedRunnable = new Runnable() {
            @Override
            public void run() {
                if (!gameOver && !paused && moleInterval > 100) moleInterval -= 50; // 更快速递减
                if (!gameOver && !paused) handler.postDelayed(this, speedIncreaseInterval);
            }
        };
        handler.postDelayed(speedRunnable, speedIncreaseInterval);
    }

    // 暂停或继续游戏
    private void togglePause() {
        if (gameOver) return;

        paused = !paused;
        if (paused) {
            // 暂停：移除所有回调
            handler.removeCallbacks(moleRunnable);
            handler.removeCallbacks(speedRunnable);
            btnPause.setText("继续");
        } else {
            // 继续：重新开启任务
            btnPause.setText("暂停");
            handler.post(moleRunnable);
            handler.postDelayed(speedRunnable, speedIncreaseInterval);
        }
    }

    // 显示地鼠
    private void showMole() {
        if (moleVisible) {
            endGame("漏点");
            return;
        }

        if (moleRow >= 0 && moleCol >= 0) {
            buttons[moleRow][moleCol].setText("");
            buttons[moleRow][moleCol].setBackgroundColor(0xFFAAAAAA);
        }

        moleRow = random.nextInt(3);
        moleCol = random.nextInt(3);
        buttons[moleRow][moleCol].setText("🐹");
        buttons[moleRow][moleCol].setBackgroundColor(0xFFFFCC00);
        moleVisible = true;
    }

    // 点击格子
    private void clickCell(int row, int col) {
        if (gameOver || paused) return;

        if (row == moleRow && col == moleCol) {
            score++;
            tvScore.setText("分数: " + score);
            buttons[moleRow][moleCol].setText("");
            buttons[moleRow][moleCol].setBackgroundColor(0xFFAAAAAA);
            moleRow = -1;
            moleCol = -1;
            moleVisible = false;
        } else {
            endGame("点错");
        }
    }

    // 游戏结束
    private void endGame(String reason) {
        if (gameOver) return; // 防止重复调用
        gameOver = true;

        if (moleRunnable != null) handler.removeCallbacks(moleRunnable);
        if (speedRunnable != null) handler.removeCallbacks(speedRunnable);

        clearAllMoles();

        // 更新最高分并保存到 SharedPreferences
        if (score > highScore) {
            highScore = score;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highScore", highScore);  // 保存最高分数
            editor.apply();  // 提交更改
        }

        // 移除最高分显示，仅显示当前得分
        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("原因: " + reason + "\n你的最终成绩: " + score) // 删除了最高分数的显示
                .setPositiveButton("开始游戏", (dialog, which) -> {
                    clearAllMoles();
                    startGame();
                })
                .setNegativeButton("退出", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!gameOver) {
            paused = true;
            handler.removeCallbacks(moleRunnable);
            handler.removeCallbacks(speedRunnable);
        }
    }
}
