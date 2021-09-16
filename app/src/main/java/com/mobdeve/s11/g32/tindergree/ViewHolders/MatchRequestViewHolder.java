package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.graphics.Bitmap;
import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class MatchRequestViewHolder extends RecyclerView.ViewHolder {
    private ImageButton ibReject, ibAccept;
    private TextView tvMrName;
    private ImageView ivMrPic;
    private Button btnMrPic;


    public MatchRequestViewHolder(@NonNull @NotNull View itemView){
        super(itemView);
        this.ivMrPic = itemView.findViewById(R.id.iv_mrPic);
        this.btnMrPic = itemView.findViewById(R.id.btn_mrPic);
        this.tvMrName = itemView.findViewById(R.id.tv_mrname);
        this.ibReject = itemView.findViewById(R.id.ib_reject);
        this.ibAccept = itemView.findViewById(R.id.ib_accept);
    }

    public void setIv_mrPic(Bitmap mrPic){
        this.ivMrPic.setImageBitmap(mrPic);
    }

    public void setTv_mrName(String mrName){
        this.tvMrName.setText(mrName);
    }

    public Button getBtn_mrPic(){
        return this.btnMrPic;
    }

    public ImageButton getIb_reject() {
        return this.ibReject;
    }

    public ImageButton getIb_accept() {
        return this.ibAccept;
    }
}
