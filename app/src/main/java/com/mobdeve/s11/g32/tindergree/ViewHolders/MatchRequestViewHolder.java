package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class MatchRequestViewHolder extends RecyclerView.ViewHolder {
    private ImageButton ib_mrPic;
    private TextView tv_mrName;

    public MatchRequestViewHolder(@NonNull @NotNull View itemView){
        super(itemView);
        this.ib_mrPic = itemView.findViewById(R.id.ib_mrPic);
        this.tv_mrName = itemView.findViewById(R.id.tv_mrname);
    }

    public void setIb_mrPic(int mrPic){
        this.ib_mrPic.setImageResource(mrPic);
    }

    public void setTv_mrName(String mrName){
        this.tv_mrName.setText(mrName);
    }

    public ImageButton getIb_mrPic(){
        return this.ib_mrPic;
    }
}
