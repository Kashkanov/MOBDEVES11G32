package com.mobdeve.s11.g32.tindergree;

public class Profile {
    private int profpicid;
    private String petname, petdesc;

    public Profile(int profpicid, String petname, String petdesc){
        this.profpicid = profpicid;
        this.petname = petname;
        this.petdesc = petdesc;
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
