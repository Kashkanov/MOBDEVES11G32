package com.mobdeve.s11.g32.tindergree;

import java.io.Serializable;

public class OtherPic implements Serializable {
    private int picid;

    public OtherPic(int picid){
        this.picid = picid;
    }

    public int getPicid(){
        return this.picid;
    }
}
