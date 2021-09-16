package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class CardViewHolder extends RecyclerView.ViewHolder{
    private ImageView ivPetPic;
    private TextView tvPetName;
    private TextView tvPetDesc;
    private ImageButton ibSeeOtherPics;
    public RecyclerView rv_otherpicarea2;

    public CardViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.ivPetPic = itemView.findViewById(R.id.iv_petpic);
        this.tvPetName = itemView.findViewById(R.id.tv_petname);
        this.tvPetDesc = itemView.findViewById(R.id.tv_petdesc);
        this.rv_otherpicarea2 = itemView.findViewById(R.id.rv_otherpicarea2);
        this.ibSeeOtherPics = itemView.findViewById(R.id.ib_seeotherpics);
    }

    public void setIv_petpic(Bitmap petpic) {
        this.ivPetPic.setImageBitmap(petpic);
    }

    public void setTv_petname(String petname){
        this.tvPetName.setText(petname);
    }

    public void setTv_petdesc(String petdesc){
        this.tvPetDesc.setText(petdesc);
    }


    public ImageButton getIb_seeotherpics(){
        return this.ibSeeOtherPics;
    }


}
