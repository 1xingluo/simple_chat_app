package com.example.myapplication3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "chat_app.db";
    private static final int DB_VERSION = 2; // 升级数据库版本

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户表
        db.execSQL("CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT)");

        // 好友关系表（双向好友）
        db.execSQL("CREATE TABLE IF NOT EXISTS friend (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "friend_id INTEGER," +
                "status INTEGER)"); // 0=请求中，1=已同意

        // 消息表（本地聊天）
        db.execSQL("CREATE TABLE IF NOT EXISTS message (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "from_id INTEGER," +
                "to_id INTEGER," +
                "content TEXT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "status INTEGER)"); // 0=未读, 1=已读
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单处理：删除重建表，可根据实际需求做迁移
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS friend");
        db.execSQL("DROP TABLE IF EXISTS message");
        onCreate(db);
    }

    // ---------------- 用户操作 ----------------
    public boolean addUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("password", password.trim());
        long result = db.insert("user", null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM user WHERE username=? AND password=?",
                new String[]{username.trim(), password.trim()});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean isUserExist(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM user WHERE username=?", new String[]{username.trim()});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getUserId(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM user WHERE username=?", new String[]{username.trim()});
        int id = -1;
        if(cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    // ---------------- 好友操作 ----------------
    public boolean addFriendRequest(int targetId, int fromId){
        SQLiteDatabase db = this.getWritableDatabase();
        // 检查是否已存在请求或已是好友
        Cursor cursor = db.rawQuery(
                "SELECT id FROM friend WHERE (user_id=? AND friend_id=?) OR (user_id=? AND friend_id=?)",
                new String[]{String.valueOf(targetId), String.valueOf(fromId),
                        String.valueOf(fromId), String.valueOf(targetId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        if(exists) return false;

        ContentValues values = new ContentValues();
        values.put("user_id", targetId); // 接收方
        values.put("friend_id", fromId); // 请求方
        values.put("status", 0); // 请求中
        long result = db.insert("friend", null, values);
        return result != -1;
    }

    public List<String> getFriendRequests(int userId){
        List<String> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.username, f.id FROM friend f JOIN user u ON f.friend_id=u.id WHERE f.user_id=? AND f.status=0",
                new String[]{String.valueOf(userId)});
        while(cursor.moveToNext()){
            requests.add(cursor.getString(0)); // 请求者用户名
        }
        cursor.close();
        return requests;
    }

    public boolean acceptFriendRequest(int userId, int friendId){
        SQLiteDatabase db = this.getWritableDatabase();
        // 更新请求状态
        ContentValues values = new ContentValues();
        values.put("status", 1);
        int updated = db.update("friend", values, "user_id=? AND friend_id=?", new String[]{String.valueOf(userId), String.valueOf(friendId)});
        if(updated > 0){
            // 双向关系
            ContentValues values2 = new ContentValues();
            values2.put("user_id", friendId);
            values2.put("friend_id", userId);
            values2.put("status", 1);
            db.insert("friend", null, values2);
            return true;
        }
        return false;
    }

    public List<String> getFriends(int userId){
        List<String> friends = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.username FROM friend f JOIN user u ON f.friend_id=u.id WHERE f.user_id=? AND f.status=1",
                new String[]{String.valueOf(userId)});
        while(cursor.moveToNext()){
            friends.add(cursor.getString(0));
        }
        cursor.close();
        return friends;
    }

    // ---------------- 消息操作 ----------------
    public boolean addMessage(int fromId, int toId, String content, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("from_id", fromId);
        values.put("to_id", toId);
        values.put("content", content);
        values.put("status", status);
        long result = db.insert("message", null, values);
        return result != -1;
    }

    public List<String> getMessages(int userId1, int userId2){
        List<String> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT from_id, content FROM message WHERE (from_id=? AND to_id=?) OR (from_id=? AND to_id=?) ORDER BY timestamp ASC",
                new String[]{String.valueOf(userId1), String.valueOf(userId2),
                        String.valueOf(userId2), String.valueOf(userId1)});
        while(cursor.moveToNext()){
            int fromId = cursor.getInt(0);
            String content = cursor.getString(1);
            messages.add((fromId==userId1?"我: ":"对方: ")+content);
        }
        cursor.close();
        return messages;
    }

}
