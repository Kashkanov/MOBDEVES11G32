package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class OtherPicsViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivOtherPic;
    
    public OtherPicsViewHolder(@NonNull @NotNull View itemView){
        super(itemView);
        this.ivOtherPic = itemView.findViewById(R.id.iv_otherpic);
    }

    public void setIv_otherpic(Bitmap otherpic){
        this.ivOtherPic.setImageBitmap(otherpic);
    }
}
