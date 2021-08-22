package com.mobdeve.s11.g32.tindergree.Models;

import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;


// TODO: DELETE THIS CLASS!
public class Profile {
    private int profpicid;
    private String petname, petdesc;
    private Boolean hasclicked; //for swipeactivity only

    public Profile(int profpicid, String petname, String petdesc){
        this.profpicid = profpicid;
        this.petname = petname;
        this.petdesc = petdesc;
        this.hasclicked = false;
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

    public Boolean getHasclicked(){
        return this.hasclicked;
    }

    public void setHasclicked(Boolean hasclicked) {
        this.hasclicked = hasclicked;
    }
}
