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
    private Button btn_matchpic;
    private TextView tv_matchname;
    private ImageView iv_matchpic;

    public MatchViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.btn_matchpic = itemView.findViewById(R.id.btn_matchpic);
        this.tv_matchname = itemView.findViewById(R.id.tv_matchname);
        this.iv_matchpic = itemView.findViewById(R.id.iv_matchpic);
    }

    public void setIv_matchpic(Bitmap matchpic) {
        this.iv_matchpic.setImageBitmap(matchpic);
    }

    public void setTv_matchname(String matchname){
        this.tv_matchname.setText(matchname);
    }

    public Button getBtn_matchpic(){
        return this.btn_matchpic;
    }

}
