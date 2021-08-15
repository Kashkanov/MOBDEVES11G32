package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class OtherPicsViewHolder extends RecyclerView.ViewHolder {
    private ImageView iv_otherpic;
    
    public OtherPicsViewHolder(@NonNull @NotNull View itemView){
        super(itemView);
        this.iv_otherpic = itemView.findViewById(R.id.iv_otherpic);
    }

    public void setIv_otherpic(int otherpic){
        this.iv_otherpic.setImageResource(otherpic);
    }
}
