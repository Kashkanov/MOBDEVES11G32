package com.mobdeve.s11.g32.tindergree.Models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class OtherPic implements Serializable {
    private Bitmap image;

    public OtherPic(Bitmap image){
        this.image = image;
    }

    public Bitmap getImageBitmap(){
        return this.image;
    }
}
