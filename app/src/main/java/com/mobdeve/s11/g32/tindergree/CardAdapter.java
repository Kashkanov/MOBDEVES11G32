package com.mobdeve.s11.g32.tindergree;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private ArrayList<Profile> profiles;
    private ImageButton ib_seeotherpics;
    private RecyclerView rv_otherpicarea2;
    public ConstraintLayout cl_buttonarea;
    private ArrayList<OtherPic> otherpics;
    public static final String KEY_PROFNAME = "KEY_PROFNAME";
    public static final String KEY_PROFPIC = "KEY_PROFPIC";
    public static final String KEY_PROFDESC = "KEY_PROFDESC";

    public CardAdapter(ArrayList<Profile> profiles){
        this.profiles = profiles;
    }

    @NonNull

    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_layout, parent, false);

        CardViewHolder cardViewHolder = new CardViewHolder(view);

        return cardViewHolder;
    }

    private boolean clicked;
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.setIv_petpic(profiles.get(position).getProfpicid());
        holder.setTv_petname(profiles.get(position).getPetname());
        holder.setTv_petdesc(profiles.get(position).getPetdesc());
        String profname = profiles.get(position).getPetname();
        String profdesc = profiles.get(position).getPetdesc();
        int profpic = profiles.get(position).getProfpicid();
        otherpics = profiles.get(position).getOtherpics();
        holder.getIb_seeotherpics().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), OtherPicsActivity.class);
                i.putExtra(KEY_PROFNAME, profname);
                i.putExtra(KEY_PROFDESC, profdesc);
                i.putExtra(KEY_PROFPIC, profpic);
                Bundle bundle = new Bundle();
                bundle.putSerializable("KEY_OTHERPICS", otherpics);
                i.putExtras(bundle);
                v.getContext().startActivity(i);
            }
        });
       /*this.clicked = true;
       holder.setIb_seeotherpicsOnclickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(clicked) {
                   Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();
                   profiles.get(position).setHasclicked(true);
                   initRecyclerView(holder, profiles.get(position).getOtherpics());
                   clicked = false;
               }
               else {
                   Toast.makeText(v.getContext(), "unclicked", Toast.LENGTH_SHORT).show();
                   profiles.get(position).setHasclicked(false);
                   clicked = true;
               }
               holder.setCls(profiles.get(position).getHasclicked());
           }
       });

    }

    private OtherPicsAdapter otherPicsAdapter;
    public void initRecyclerView(CardViewHolder holder, ArrayList<OtherPic> otherpics){

        LinearLayoutManager manager = new LinearLayoutManager(holder.rv_otherpicarea2.getContext(),LinearLayoutManager.HORIZONTAL, false);
        holder.rv_otherpicarea2.setLayoutManager(manager);
        otherPicsAdapter = new OtherPicsAdapter(otherpics);
        holder.rv_otherpicarea2.setAdapter(otherPicsAdapter);
    }
*/
    }
    @Override
    public int getItemCount() {
        return this.profiles.size();
    }
}
