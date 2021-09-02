package com.mobdeve.s11.g32.tindergree.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.Models.Chat;
import com.mobdeve.s11.g32.tindergree.R;
import com.mobdeve.s11.g32.tindergree.ViewHolders.CardViewHolder;
import com.mobdeve.s11.g32.tindergree.ViewHolders.ChatViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private ArrayList<Chat> chatDataset;

    public ChatAdapter(ArrayList<Chat> chatDataset) { this.chatDataset = chatDataset; }

    @NonNull
    @NotNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_layout, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatViewHolder holder, int position) {
        holder.setTv_sender(chatDataset.get(position).getSender());
        holder.setTv_message(chatDataset.get(position).getMessage());
    }

    @Override
    public int getItemCount() { return this.chatDataset.size(); }
}
