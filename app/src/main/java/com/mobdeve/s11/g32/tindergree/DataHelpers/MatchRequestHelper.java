package com.mobdeve.s11.g32.tindergree.DataHelpers;

import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class MatchRequestHelper {
    public static ArrayList<MatchRequest> loadMatchRequestData(){
        ArrayList<MatchRequest> mrs = new ArrayList<MatchRequest>();
        mrs.add(new MatchRequest(new Profile(R.mipmap.dog6_foreground,"Garfield","Wait isn't he a cat??")));
        mrs.add(new MatchRequest(new Profile(R.mipmap.dog7_foreground,"TheEnlightedOne","HEHEHEHAHAHAHA")));
        return mrs;
    };
}
