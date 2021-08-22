package com.mobdeve.s11.g32.tindergree.Models;

import android.graphics.Bitmap;

import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class CardProfile {
    private Bitmap userProfilePicture;
    private String uid;
    private String petname, petdesc;
    private Boolean hasclicked; //for swipeactivity only

    public CardProfile(Bitmap userProfilePicture, String petname, String petdesc, String uid){
        this.userProfilePicture = userProfilePicture;
        this.petname = petname;
        this.petdesc = petdesc;
        this.uid = uid;

        this.hasclicked = false;
    }

    public CardProfile(String petname, String petdesc, String uid){
        this.petname = petname;
        this.petdesc = petdesc;
        this.uid = uid;

        this.hasclicked = false;
    }

    public Bitmap getuserProfilePicture() {
        return this.userProfilePicture;
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

    public String getUid(){
        return this.uid;
    }

    public void setHasclicked(Boolean hasclicked) {
        this.hasclicked = hasclicked;
    }

    public void setUserProfilePicture(Bitmap profilePicture) { this.userProfilePicture = profilePicture; }
}
