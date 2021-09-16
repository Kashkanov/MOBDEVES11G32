package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class MatchViewHolder extends RecyclerView.ViewHolder {
    private Button btnMatchPic;
    private TextView tvMatchName;
    private ImageView ivMatchPic;

    public MatchViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.btnMatchPic = itemView.findViewById(R.id.btn_matchpic);
        this.tvMatchName = itemView.findViewById(R.id.tv_matchname);
        this.ivMatchPic = itemView.findViewById(R.id.iv_matchpic);
    }

    public void setIv_matchpic(Bitmap matchpic) {
        this.ivMatchPic.setImageBitmap(matchpic);
    }

    public void setTv_matchname(String matchname){
        this.tvMatchName.setText(matchname);
    }

    public Button getBtn_matchpic(){
        return this.btnMatchPic;
    }

}
