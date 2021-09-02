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
    private String uid;
    private String kachatName;

    public ChatAdapter(ArrayList<Chat> chatDataset, String uid, String kachatName) {
        this.chatDataset = chatDataset;
        this.uid = uid;
        this.kachatName = kachatName;
    }

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
        if (chatDataset.get(position).getSender().compareTo(uid) == 0)
            holder.setTv_sender("You:");
        else
            holder.setTv_sender(kachatName + ":");

        holder.setTv_message(chatDataset.get(position).getMessage());
    }

    @Override
    public int getItemCount() { return this.chatDataset.size(); }
}
