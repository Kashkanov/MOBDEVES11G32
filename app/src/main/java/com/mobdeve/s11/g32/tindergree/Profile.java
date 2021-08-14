package com.mobdeve.s11.g32.tindergree;

import java.util.ArrayList;

public class Profile {
    private int profpicid;
    private ArrayList<Integer> otherpics;
    private String petname, petdesc;

    public Profile(int profpicid, String petname, String petdesc){
        this.profpicid = profpicid;
        this.petname = petname;
        this.petdesc = petdesc;
        this.otherpics = new ArrayList<Integer>();
        this.otherpics.add(R.mipmap.dog3_foreground);
        this.otherpics.add(R.mipmap.dog4_foreground);
        this.otherpics.add(R.mipmap.dog5_foreground);

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
}
