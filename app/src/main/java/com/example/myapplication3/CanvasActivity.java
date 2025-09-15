package com.example.myapplication3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private List<String> allUsers;
    private Map<String, List<String>> friendMap; // key=用户名, value=好友列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);

        // 获取所有用户
        allUsers = dbHelper.getAllUsernames();

        // 构建好友映射
        friendMap = new HashMap<>();
        for (String user : allUsers) {
            int userId = dbHelper.getUserId(user);
            friendMap.put(user, dbHelper.getFriends(userId));
        }

        setContentView(new NetworkView(this));
    }

    /** 自定义视图绘制好友网络 */
    private class NetworkView extends View {
        Paint paint = new Paint();

        public NetworkView(Context context) {
            super(context);
            paint.setAntiAlias(true);
            paint.setTextSize(30f);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);

            if (allUsers == null || allUsers.isEmpty()) {
                paint.setColor(Color.BLACK);
                canvas.drawText("暂无用户数据", 50, 100, paint);
                return;
            }

            int width = getWidth();
            int height = getHeight();
            int radius = Math.min(width, height) / 3;

            // 计算圆形布局节点坐标
            Map<String, Float[]> nodePos = new HashMap<>();
            int n = allUsers.size();
            float centerX = width / 2f;
            float centerY = height / 2f;

            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n;
                float x = centerX + (float) (radius * Math.cos(angle));
                float y = centerY + (float) (radius * Math.sin(angle));
                nodePos.put(allUsers.get(i), new Float[]{x, y});
            }

            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(3f);

            // 绘制好友连线
            for (String user : allUsers) {
                List<String> friends = friendMap.get(user);
                if (friends == null) continue;
                Float[] p1 = nodePos.get(user);
                for (String friend : friends) {
                    if (!nodePos.containsKey(friend)) continue;
                    Float[] p2 = nodePos.get(friend);
                    // 避免重复画线
                    if (user.compareTo(friend) < 0) {
                        canvas.drawLine(p1[0], p1[1], p2[0], p2[1], paint);
                    }
                }
            }

            // 绘制节点
            int nodeSize = 80;
            paint.setColor(Color.CYAN);
            paint.setStrokeWidth(2f);
            for (String user : allUsers) {
                Float[] pos = nodePos.get(user);
                float x = pos[0];
                float y = pos[1];

                // 画圆
                canvas.drawCircle(x, y, nodeSize / 2f, paint);

                // 画头像（如果存在）
                File avatarFile = new File(getFilesDir(), user + "_avatar.png");
                if (avatarFile.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
                    Bitmap scaled = Bitmap.createScaledBitmap(bmp, nodeSize, nodeSize, false);
                    canvas.drawBitmap(scaled, x - nodeSize / 2f, y - nodeSize / 2f, null);
                }

                // 画名字
                paint.setColor(Color.BLACK);
                paint.setTextSize(30f);
                float textWidth = paint.measureText(user);
                canvas.drawText(user, x - textWidth / 2, y + nodeSize / 2 + 30, paint);

                // 恢复圆颜色
                paint.setColor(Color.CYAN);
            }
        }
    }
}
