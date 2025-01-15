package com.example.symptom_checker;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SentMessageViewHolder extends RecyclerView.ViewHolder {
    TextView textMessage, textDateTime;

    public SentMessageViewHolder(View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.textMessage);
        textDateTime = itemView.findViewById(R.id.textDateTime);
    }
}
