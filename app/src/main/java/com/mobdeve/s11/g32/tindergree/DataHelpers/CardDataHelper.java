package com.mobdeve.s11.g32.tindergree.DataHelpers;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.Activities.SwipeActivity;
import com.mobdeve.s11.g32.tindergree.Models.CardProfile;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CardDataHelper {

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    public static ArrayList<Profile> loadProfileData(){
        ArrayList<Profile> profiles = new ArrayList<Profile>();

        profiles.add(new Profile(R.mipmap.pic1_foreground,"Bruno","I luv belly pats"));
        profiles.add(new Profile(R.mipmap.dog2_foreground,"Daisy","woof woof"));
        profiles.add(new Profile(R.mipmap.dog3_foreground,"Alpha","P O R K C H O P S"));
        profiles.add(new Profile(R.mipmap.dog4_foreground,"Bravo","P O R K C H O P S"));
        profiles.add(new Profile(R.mipmap.dog5_foreground,"Charlie","P O R K C H O P S"));
        profiles.add(new Profile(R.mipmap.dog6_foreground,"Delta","P O R K C H O P S"));
        profiles.add(new Profile(R.mipmap.dog7_foreground,"Echo","P O R K C H O P S"));
        return profiles;
    };

    /**
     * Fetches user profiles from Firebase.
     * @return
     */
    public ArrayList<CardProfile> loadProfiles() {
        // The card area corresponds to the matches of the user.

        ArrayList<CardProfile> profiles = new ArrayList<>();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Comment these lines if production Firebase should be used instead of emulator
        try {
            FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
            firestore.useEmulator("10.0.2.2", 8080);
        }
        catch (IllegalStateException e) {
            Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
        }

        // TODO: Load data from Firebase.


        return profiles;
    }

}
