package com.mobdeve.s11.g32.tindergree.ViewHolders;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

public class MatchViewHolder extends RecyclerView.ViewHolder {
    private ImageButton ib_matchpic;
    private TextView tv_matchname;

    public MatchViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.ib_matchpic = itemView.findViewById(R.id.ib_matchpic);
        this.tv_matchname = itemView.findViewById(R.id.tv_matchname);
    }

    public void setIb_matchpic(Bitmap matchpic) {
        this.ib_matchpic.setImageBitmap(matchpic);
    }

    public void setTv_matchname(String matchname){
        this.tv_matchname.setText(matchname);
    }

    public ImageButton getIb_matchpic(){
        return this.ib_matchpic;
    }

}
