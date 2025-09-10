package com.example.myapplication3;

public class ChatMessage {

    public static final int TYPE_SENT = 0;      // 我发送的消息
    public static final int TYPE_RECEIVED = 1;  // 对方发送的消息

    private String content;  // 消息内容
    private int type;        // 消息类型

    public ChatMessage(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}

