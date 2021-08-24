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
    private ImageButton ib_reject, ib_accept;
    private TextView tv_mrName;
    private ImageView iv_mrPic;
    private Button btn_mrPic;


    public MatchRequestViewHolder(@NonNull @NotNull View itemView){
        super(itemView);
        this.iv_mrPic = itemView.findViewById(R.id.iv_mrPic);
        this.btn_mrPic = itemView.findViewById(R.id.btn_mrPic);
        this.tv_mrName = itemView.findViewById(R.id.tv_mrname);
        this.ib_reject = itemView.findViewById(R.id.ib_reject);
        this.ib_accept = itemView.findViewById(R.id.ib_accept);
    }

    public void setIv_mrPic(Bitmap mrPic){
        this.iv_mrPic.setImageBitmap(mrPic);
    }

    public void setTv_mrName(String mrName){
        this.tv_mrName.setText(mrName);
    }

    public Button getBtn_mrPic(){
        return this.btn_mrPic;
    }

    public ImageButton getIb_reject() {
        return this.ib_reject;
    }

    public ImageButton getIb_accept() {
        return this.ib_accept;
    }
}
