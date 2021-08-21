package com.mobdeve.s11.g32.tindergree.Models;

public class MatchRequest {
    private Profile profile;
    private Boolean status;

    public MatchRequest(Profile profile){
        this.profile = profile;
    }

    public Profile getProfile(){
        return this.profile;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public boolean getStatus(){
        return this.status;
    }
}
