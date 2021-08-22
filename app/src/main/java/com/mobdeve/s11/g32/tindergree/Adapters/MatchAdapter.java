package com.mobdeve.s11.g32.tindergree.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.Activities.ChatActivity;
import com.mobdeve.s11.g32.tindergree.Models.CardProfile;
import com.mobdeve.s11.g32.tindergree.ViewHolders.MatchViewHolder;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchViewHolder> {
    private ArrayList<CardProfile> profiles;

    public static String KEY_MATCHPIC = "KEY_MATCHPIC";
    public static String KEY_MATCHNAME = "KEY_MATCHNAME";
    public static String KEY_MATCHDESC = "KEY_MATCHDESC";
    public static final String KEY_UID = "KEY_UID";

    public MatchAdapter(ArrayList<CardProfile> profiles){
        this.profiles = profiles;
    }

    @NonNull

    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.matches_layout, parent, false);

        MatchViewHolder matchViewHolder = new MatchViewHolder(view);

        return matchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        holder.setIb_matchpic(profiles.get(position).getuserProfilePicture());
        holder.setTv_matchname(profiles.get(position).getPetname());

        holder.getIb_matchpic().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ChatActivity.class);
                i.putExtra(KEY_MATCHNAME, profiles.get(position).getPetname());
                i.putExtra(KEY_MATCHDESC, profiles.get(position).getPetdesc());
                i.putExtra(KEY_UID, profiles.get(position).getUid());
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.profiles.size();
    }
}
