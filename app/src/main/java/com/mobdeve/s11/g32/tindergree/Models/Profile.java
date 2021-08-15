package com.mobdeve.s11.g32.tindergree.Models;

import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class Profile {
    private int profpicid;
    private ArrayList<OtherPic> otherpics;
    private String petname, petdesc;
    private Boolean hasclicked; //for swipeactivity only

    public Profile(int profpicid, String petname, String petdesc){
        this.profpicid = profpicid;
        this.petname = petname;
        this.petdesc = petdesc;
        this.hasclicked = false;
        this.otherpics = new ArrayList<OtherPic>();
        this.otherpics.add(new OtherPic(R.mipmap.dog3_foreground));
        this.otherpics.add(new OtherPic(R.mipmap.dog4_foreground));
        this.otherpics.add(new OtherPic(R.mipmap.dog5_foreground));

    }

    public int getProfpicid() {
        return this.profpicid;
    }

    public String getPetname() {
        return this.petname;
    }

    public String getPetdesc(){
        return this.petdesc;
    }

    public ArrayList<OtherPic> getOtherpics() {
        return this.otherpics;
    }

    public Boolean getHasclicked(){
        return this.hasclicked;
    }

    public void setHasclicked(Boolean hasclicked) {
        this.hasclicked = hasclicked;
    }
}
