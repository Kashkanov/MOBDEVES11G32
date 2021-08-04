package com.mobdeve.s11.g32.tindergree;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class CardViewHolder extends RecyclerView.ViewHolder{
    private ImageView iv_petpic;
    private TextView tv_petname;
    private TextView tv_petdesc;

    public CardViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.iv_petpic = itemView.findViewById(R.id.iv_petpic);
        this.tv_petname = itemView.findViewById(R.id.tv_petname);
        this.tv_petdesc = itemView.findViewById(R.id.tv_petdesc);
    }

    public void setIv_petpic(int petpic) {
        this.iv_petpic.setImageResource(petpic);
    }

    public void setTv_petname(String petname){
        this.tv_petname.setText(petname);
    }

    public void setTv_petdesc(String petdesc){
        this.tv_petdesc.setText(petdesc);
    }
}
