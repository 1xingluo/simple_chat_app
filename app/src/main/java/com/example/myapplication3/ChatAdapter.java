package com.example.myapplication3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends ArrayAdapter<DBHelper.MessageItem> {

    private String currentUsername;

    public ChatAdapter(Context context, List<DBHelper.MessageItem> messages, String currentUsername){
        super(context, 0, messages);
        this.currentUsername = currentUsername;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        DBHelper.MessageItem item = getItem(position);
        boolean isMe = item.senderName.equals(currentUsername);

        if(isMe){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.chat_item_right, parent, false);
        } else {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.chat_item_left, parent, false);
        }

        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
        String timeStr = formatTime(item.timestamp);
        tvMessage.setText(item.senderName + ": " + item.content + " (" + timeStr + ")");
        return convertView;
    }

    private String formatTime(String timestamp){
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf1.parse(timestamp);
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf2.format(date);
        } catch (ParseException e){
            e.printStackTrace();
            return timestamp;
        }
    }
}
