package com.mobdeve.s11.g32.tindergree;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private ArrayList<Profile> profiles;

    public CardAdapter(ArrayList<Profile> profiles){
        this.profiles = profiles;
    }

    @NonNull

    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_layout, parent, false);

        CardViewHolder tweetViewHolder = new CardViewHolder(view);

        /*
        tweetViewHolder.getIb_profpic().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), TweetDetailsActivity.class);
                i.putExtra(KEY_DISPLAYNAME,tweets.get(tweetViewHolder.getBindingAdapterPosition()).getDisplayName());
                i.putExtra(KEY_PIC,tweets.get(tweetViewHolder.getBindingAdapterPosition()).getImageId());
                i.putExtra(KEY_HANDLER,tweets.get(tweetViewHolder.getBindingAdapterPosition()).getUserHandle());
                i.putExtra(KEY_TWEET,tweets.get(tweetViewHolder.getBindingAdapterPosition()).getCaption());
                i.putExtra(KEY_DATE,tweets.get(tweetViewHolder.getBindingAdapterPosition()).getCreatedAt().toStringFull());
                i.putExtra(KEY_LIKES,new Integer(tweets.get(tweetViewHolder.getBindingAdapterPosition()).getLikes()).toString());

                view.getContext().startActivity(i);
            }
        });*/
        return tweetViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
       holder.setIv_petpic(profiles.get(position).getProfpicid());
       holder.setTv_petname(profiles.get(position).getPetname());
       holder.setTv_petdesc(profiles.get(position).getPetdesc());

    }

    @Override
    public int getItemCount() {
        return this.profiles.size();
    }
}
