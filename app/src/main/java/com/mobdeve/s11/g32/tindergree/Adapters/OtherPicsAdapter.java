package com.mobdeve.s11.g32.tindergree.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.Models.OtherPic;
import com.mobdeve.s11.g32.tindergree.ViewHolders.OtherPicsViewHolder;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class OtherPicsAdapter extends RecyclerView.Adapter<OtherPicsViewHolder> {
    private ArrayList<OtherPic> otherpics;

    public OtherPicsAdapter(ArrayList<OtherPic> otherpics){
        this.otherpics = otherpics;
    }

    @NonNull
    @Override
    public OtherPicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.otherpics_layout, parent, false);

        OtherPicsViewHolder otherPicsViewHolder = new OtherPicsViewHolder(view);

        return otherPicsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OtherPicsViewHolder holder, int position) {
        holder.setIv_otherpic(otherpics.get(position).getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return this.otherpics.size();
    }
}
