package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    private TextView tvSender;
    private TextView tvMessage;

    public ChatViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvSender = itemView.findViewById(R.id.tv_sender);
        this.tvMessage = itemView.findViewById(R.id.tv_message);
    }

    public void setTv_sender(String sender) {
        this.tvSender.setText(sender);
    }

    public void setTv_message(String message) {
        this.tvMessage.setText(message);
    }

}
