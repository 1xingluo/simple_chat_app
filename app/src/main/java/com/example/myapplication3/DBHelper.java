package com.example.myapplication3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "chat_app.db";
    private static final int DB_VERSION = 2;

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

        // 好友表
        db.execSQL("CREATE TABLE IF NOT EXISTS friend (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "friend_id INTEGER," +
                "status INTEGER)");

        // 消息表
        db.execSQL("CREATE TABLE IF NOT EXISTS message (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "from_id INTEGER," +
                "to_id INTEGER," +
                "content TEXT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "status INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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

    public String getUsername(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username FROM user WHERE id=?", new String[]{String.valueOf(userId)});
        String name = "";
        if(cursor.moveToFirst()){
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    // ---------------- 好友操作 ----------------
    public boolean addFriendRequest(int targetId, int fromId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM friend WHERE (user_id=? AND friend_id=?) OR (user_id=? AND friend_id=?)",
                new String[]{String.valueOf(targetId), String.valueOf(fromId),
                        String.valueOf(fromId), String.valueOf(targetId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        if(exists) return false;

        ContentValues values = new ContentValues();
        values.put("user_id", targetId);   // 接收方
        values.put("friend_id", fromId);   // 发起方
        values.put("status", 0);           // 请求中
        long result = db.insert("friend", null, values);
        return result != -1;
    }

    public boolean rejectFriendRequest(int userId, int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete("friend", "user_id=? AND friend_id=? AND status=0",
                new String[]{String.valueOf(userId), String.valueOf(friendId)});
        return deleted > 0;
    }

    public boolean acceptFriendRequest(int userId, int friendId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", 1);
        int updated = db.update("friend", values,
                "user_id=? AND friend_id=?",
                new String[]{String.valueOf(userId), String.valueOf(friendId)});
        if(updated > 0){
            ContentValues values2 = new ContentValues();
            values2.put("user_id", friendId);
            values2.put("friend_id", userId);
            values2.put("status", 1);
            db.insert("friend", null, values2);
            return true;
        }
        return false;
    }

    public List<String> getFriendRequests(int userId){
        List<String> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.username FROM friend f JOIN user u ON f.friend_id=u.id " +
                        "WHERE f.user_id=? AND f.status=0",
                new String[]{String.valueOf(userId)});
        while(cursor.moveToNext()){
            requests.add(cursor.getString(0));
        }
        cursor.close();
        return requests;
    }

    public List<String> getSentRequests(int userId){
        List<String> sent = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.username FROM friend f JOIN user u ON f.user_id=u.id " +
                        "WHERE f.friend_id=? AND f.status=0",
                new String[]{String.valueOf(userId)});
        while(cursor.moveToNext()){
            sent.add(cursor.getString(0));
        }
        cursor.close();
        return sent;
    }

    public List<String> getFriends(int userId){
        List<String> friends = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.username FROM friend f JOIN user u ON f.friend_id=u.id " +
                        "WHERE f.user_id=? AND f.status=1",
                new String[]{String.valueOf(userId)});
        while(cursor.moveToNext()){
            friends.add(cursor.getString(0));
        }
        cursor.close();
        return friends;
    }

    // ---------------- 消息操作 ----------------
    public boolean sendMessage(int fromId, int toId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("from_id", fromId);
        values.put("to_id", toId);
        values.put("content", content);
        values.put("status", 0); // 未读
        long result = db.insert("message", null, values);
        return result != -1;
    }

    public List<String> getMessages(int userId, int friendId) {
        List<String> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT from_id, content, timestamp FROM message " +
                        "WHERE (from_id=? AND to_id=?) OR (from_id=? AND to_id=?) " +
                        "ORDER BY timestamp ASC",
                new String[]{String.valueOf(userId), String.valueOf(friendId),
                        String.valueOf(friendId), String.valueOf(userId)});
        while (cursor.moveToNext()) {
            int from = cursor.getInt(0);
            String content = cursor.getString(1);
            String time = cursor.getString(2);
            String msg = (from == userId ? "我: " : "对方: ") + content + " (" + time + ")";
            messages.add(msg);
        }
        cursor.close();
        return messages;
    }

    public void markMessagesAsRead(int userId, int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", 1);
        db.update("message", values,
                "to_id=? AND from_id=? AND status=0",
                new String[]{String.valueOf(userId), String.valueOf(friendId)});
    }

    // ---------------- 私聊显示用户名 ----------------
    public static class MessageItem {
        public String senderName;
        public String content;
        public String timestamp;
        public MessageItem(String senderName, String content, String timestamp){
            this.senderName = senderName;
            this.content = content;
            this.timestamp = timestamp;
        }
    }

    public List<MessageItem> getMessagesWithUsername(int userId, int friendId) {
        List<MessageItem> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT from_id, content, timestamp FROM message " +
                        "WHERE (from_id=? AND to_id=?) OR (from_id=? AND to_id=?) " +
                        "ORDER BY timestamp ASC",
                new String[]{String.valueOf(userId), String.valueOf(friendId),
                        String.valueOf(friendId), String.valueOf(userId)});
        while (cursor.moveToNext()) {
            int from = cursor.getInt(0);
            String content = cursor.getString(1);
            String time = cursor.getString(2);
            String senderName = getUsername(from);
            messages.add(new MessageItem(senderName, content, time));
        }
        cursor.close();
        return messages;
    }
}
