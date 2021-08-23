package com.mobdeve.s11.g32.tindergree.Models;

// For populating the Recycler View
public class MatchRequest {
    private CardProfile profile;
    private Boolean status;

    public MatchRequest(CardProfile profile){
        this.profile = profile;
    }

    public CardProfile getProfile(){
        return this.profile;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public boolean getStatus(){
        return this.status;
    }
}
