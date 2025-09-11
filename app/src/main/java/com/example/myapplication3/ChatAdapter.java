package com.example.myapplication3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<ChatMessage> messageList;

    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    // 告诉ListView item有几种类型
    @Override
    public int getViewTypeCount() {
        return 2; // TYPE_RECEIVED 和 TYPE_SENT
    }

    // 返回每个item的类型
    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType();
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
        TextView tvMessage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage msg = messageList.get(position);
        ViewHolder holder;

        if (convertView == null) {
            if (msg.getType() == ChatMessage.TYPE_RECEIVED) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_received, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_sent, parent, false);
            }
            holder = new ViewHolder();
            holder.tvMessage = convertView.findViewById(R.id.tv_message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvMessage.setText(msg.getContent());
        return convertView;
    }
}