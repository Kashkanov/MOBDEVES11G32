package com.mobdeve.s11.g32.tindergree.Models;

import android.graphics.Bitmap;

import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class CardProfile {
    private Bitmap userProfilePicture;
    private String uid;
    private ArrayList<Bitmap> otherpics;
    private String petname, petdesc;
    private Boolean hasclicked; //for swipeactivity only

    public CardProfile(Bitmap userProfilePicture, String petname, String petdesc, String uid){
        this.userProfilePicture = userProfilePicture;
        this.petname = petname;
        this.petdesc = petdesc;
        this.hasclicked = false;
        this.otherpics = new ArrayList<Bitmap>();
        this.uid = uid;
    }

    private void addOtherPics() {
        // Cloud Storage and Firestore implementation
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

    public ArrayList<Bitmap> getOtherpics() { return this.otherpics; }

    public Boolean getHasclicked(){
        return this.hasclicked;
    }

    public void setHasclicked(Boolean hasclicked) {
        this.hasclicked = hasclicked;
    }
}
