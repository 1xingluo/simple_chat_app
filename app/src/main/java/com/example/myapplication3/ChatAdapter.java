package com.example.myapplication3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<ChatMessage> messageList;

    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        LinearLayout layoutMessage;
        TextView tvMessage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
            holder = new ViewHolder();
            holder.layoutMessage = convertView.findViewById(R.id.layout_message);
            holder.tvMessage = convertView.findViewById(R.id.tv_message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatMessage msg = messageList.get(position);

        holder.tvMessage.setText(msg.getContent());

        // 根据消息类型设置背景
        if (msg.getType() == ChatMessage.TYPE_SENT) {
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_sent);
            holder.layoutMessage.setGravity(android.view.Gravity.END);
        } else {
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_received);
            holder.layoutMessage.setGravity(android.view.Gravity.START);
        }

        return convertView;
    }
}
