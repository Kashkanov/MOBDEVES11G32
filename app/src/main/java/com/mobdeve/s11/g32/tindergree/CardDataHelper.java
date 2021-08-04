package com.mobdeve.s11.g32.tindergree;

import java.util.ArrayList;

public class CardDataHelper {
    public static ArrayList<Profile> loadProfileData(){
        ArrayList<Profile> profiles = new ArrayList<Profile>();

        profiles.add(new Profile(R.mipmap.pic1_foreground,"Bruno","I luv belly pats"));
        profiles.add(new Profile(R.mipmap.dog2_foreground,"Daisy","woof woof"));
        profiles.add(new Profile(R.mipmap.dog3_foreground,"Alpha","P O R K C H O P S"));

        return profiles;
    };

}
