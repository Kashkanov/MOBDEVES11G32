package com.mobdeve.s11.g32.tindergree;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchViewHolder> {
    private ArrayList<Profile> profiles;

    public static String KEY_MATCHPIC = "KEY_MATCHPIC";
    public static String KEY_MATCHNAME = "KEY_MATCHNAME";
    public static String KEY_MATCHDESC = "KEY_MATCHDESC";

    public MatchAdapter(ArrayList<Profile> profiles){
        this.profiles = profiles;
    }

    @NonNull

    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.matches_layout, parent, false);

        MatchViewHolder matchViewHolder = new MatchViewHolder(view);


        matchViewHolder.getIb_matchpic().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), ChatActivity.class);
                i.putExtra(KEY_MATCHPIC,profiles.get(matchViewHolder.getBindingAdapterPosition()).getProfpicid());
                i.putExtra(KEY_MATCHNAME,profiles.get(matchViewHolder.getBindingAdapterPosition()).getPetname());
                i.putExtra(KEY_MATCHDESC,profiles.get(matchViewHolder.getBindingAdapterPosition()).getPetdesc());
                Bundle bundle = new Bundle();
                bundle.putSerializable("KEY_OTHERPICS",profiles.get(matchViewHolder.getBindingAdapterPosition()).getOtherpics());
                i.putExtras(bundle);
                view.getContext().startActivity(i);
            }
        });
        return matchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        holder.setIb_matchpic(profiles.get(position).getProfpicid());
        holder.setTv_matchname(profiles.get(position).getPetname());

    }

    @Override
    public int getItemCount() {
        return this.profiles.size();
    }
}
