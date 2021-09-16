package com.mobdeve.s11.g32.tindergree.DataHelpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g32.tindergree.Activities.MatchRequestsActivity;
import com.mobdeve.s11.g32.tindergree.Activities.SwipeActivity;
import com.mobdeve.s11.g32.tindergree.Models.CardProfile;
import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.Models.Matches;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MatchRequestHelper {

    private static FirebaseAuth mAuth;
    private static FirebaseStorage storage;
    private static FirebaseFirestore firestore;

    private static int maxNumberOfRequests;

    public static void loadMatchRequestData(MatchRequestsActivity matchRequestsActivity){
        ArrayList<MatchRequest> mrs = new ArrayList<MatchRequest>();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.clearPersistence();

        // Comment these lines if production Firebase should be used instead of emulator
        if (SwipeActivity.useEmulator) {
            try {
                FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
                FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
                firestore.useEmulator("10.0.2.2", 8080);
            } catch (IllegalStateException e) {
                Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
            }
        }

        Log.d(SwipeActivity.firebaseLogKey, "Now attempting to get all match requests...");

        String userUid = mAuth.getUid();

        // Get Matches record of the current user from Firestore. This provides the incoming match requests.
        firestore.collection("Matches")
                .whereEqualTo("uid", userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(SwipeActivity.firebaseLogKey, "Got Match record!");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Save the Matches record (for updating the database later on)
                                String uid = userUid;
                                ArrayList<String> uidMatches = (ArrayList<String>) document.getData().get("uidMatches");
                                ArrayList<String> uidMatchRequests = (ArrayList<String>) document.getData().get("uidMatchRequests");

                                Matches matches = new Matches(uid, uidMatches, uidMatchRequests);
                                matchRequestsActivity.matches = matches;

                                maxNumberOfRequests = matchRequestsActivity.matches.uidMatchRequests.size();

                                // No matches
                                if (maxNumberOfRequests == 0) {
                                    Log.d(SwipeActivity.firebaseLogKey, "No incoming match requests found.");
                                    return;
                                }

                                // For each incoming match requests, get the pet names and image filenames
                                for (String matchUid: matchRequestsActivity.matches.uidMatchRequests) {
                                    Log.d(SwipeActivity.firebaseLogKey, "Attempting to retrieve UID: " + matchUid);
                                    // First, get the pet name and description
                                    firestore.collection("Pets")
                                            .whereEqualTo("uid", matchUid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        // For each incoming match, get their information and display to the view
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String petName = document.getData().get("petName").toString();
                                                            String petDescription = document.getData().get("petDescription").toString();

                                                            // Next, get the filename
                                                            firestore.collection("filenames")
                                                                    .whereEqualTo("uid", matchUid)
                                                                    .whereEqualTo("isProfilePicture", true)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                    Log.d(SwipeActivity.firebaseLogKey, document.getId() + " => " + document.getData());
                                                                                    String filename = document.getData().get("filename").toString();

                                                                                    // Lastly, fetch the image
                                                                                    MatchRequest matchRequest = new MatchRequest(new CardProfile(petName, petDescription, matchUid));
                                                                                    fetchImage(matchUid, matchRequest, matchRequestsActivity);
                                                                                }
                                                                            } else {
                                                                                Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    } else {
                                                        Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            // Handle errors
                        }
                    }
                });
    };

    public static void fetchImage(String matchUid, MatchRequest matchRequest, MatchRequestsActivity matchRequestsActivity) {
        firestore.clearPersistence();

        // Get the filename of the candidate's profile picture
        firestore.collection("filenames")
                .whereEqualTo("uid", matchRequest.getProfile().getUid())
                .whereEqualTo("isProfilePicture", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) { // Got the UID and filename of the profile picture
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String candidateUid = document.getData().get("uid").toString();
                                String candidatePictureFilename = document.getData().get("filename").toString();

                                // From the UID and filename, get the profile image
                                StorageReference storageRef = storage.getReference();

                                StorageReference candidatePictureRef = storageRef.child("Users/" + candidateUid + "/" + candidatePictureFilename);
                                Log.d(SwipeActivity.firebaseLogKey, "On path:" + candidatePictureRef.getPath());

                                final long MAX_BYE_SIZE = 1024 * 10024;
                                candidatePictureRef.getBytes(MAX_BYE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Log.d(SwipeActivity.firebaseLogKey, "Profile picture fetched for " + candidateUid);
                                        // Data for the image is returned, use this as needed
                                        Bitmap image = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                                        matchRequest.getProfile().setUserProfilePicture(image);

                                        matchRequestsActivity.matchRequests.add(matchRequest);
                                        Log.d(SwipeActivity.firebaseLogKey, "Saved profile picture!");

                                        maxNumberOfRequests--;

                                        if (maxNumberOfRequests == 0) {
                                            Log.d(SwipeActivity.firebaseLogKey, "Successfully fetched all match requests!");
                                            matchRequestsActivity.finalizeRecyclerView();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        Log.d(SwipeActivity.firebaseLogKey, "Failed to fetch profile picture from Cloud Storage");
                                    }
                                });
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
