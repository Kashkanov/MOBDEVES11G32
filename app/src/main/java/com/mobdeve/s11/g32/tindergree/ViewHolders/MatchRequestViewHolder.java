package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.graphics.Bitmap;
import android.media.Image;
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
    private ImageButton ib_mrPic, ib_reject, ib_accept;
    private TextView tv_mrName;

    public MatchRequestViewHolder(@NonNull @NotNull View itemView){
        super(itemView);
        this.ib_mrPic = itemView.findViewById(R.id.ib_mrPic);
        this.tv_mrName = itemView.findViewById(R.id.tv_mrname);
        this.ib_reject = itemView.findViewById(R.id.ib_reject);
        this.ib_accept = itemView.findViewById(R.id.ib_accept);
    }

    public void setIb_mrPic(Bitmap mrPic){
        this.ib_mrPic.setImageBitmap(mrPic);
    }

    public void setTv_mrName(String mrName){
        this.tv_mrName.setText(mrName);
    }

    public ImageButton getIb_mrPic(){
        return this.ib_mrPic;
    }

    public ImageButton getIb_reject() {
        return this.ib_reject;
    }

    public ImageButton getIb_accept() {
        return this.ib_accept;
    }
}
