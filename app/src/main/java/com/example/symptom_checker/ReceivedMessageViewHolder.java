package com.example.symptom_checker;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
    TextView textMessage;

    public ReceivedMessageViewHolder(View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.textMessage);
    }
}