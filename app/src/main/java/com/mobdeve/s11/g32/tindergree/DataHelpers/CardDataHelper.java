package com.mobdeve.s11.g32.tindergree.DataHelpers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g32.tindergree.Activities.DisplayChatsActivity;
import com.mobdeve.s11.g32.tindergree.Activities.SwipeActivity;
import com.mobdeve.s11.g32.tindergree.Models.CardProfile;
import com.mobdeve.s11.g32.tindergree.Models.Matches;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CardDataHelper {

    private Query cardQuery;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private String currAnimal;

    final int[] numberOfCandidates = new int[1];


    /**
     * Fetches user profiles from Firebase.
     */
    public void loadProfiles(SwipeActivity swipeActivity, ArrayList<CardProfile> profiles2) {
        // The card area corresponds to the matches of the user.

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

        Log.d(SwipeActivity.firebaseLogKey, "Now attempting to get candidates...");

        this.getUserAnimal();

        // Fetch the UIDs of matched users (to know which ones to exclude in the swipe cards)
        firestore.collection("Matches")
                .whereEqualTo("uid", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot x = task.getResult();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // These are the UIDs to exclude from the card swipes
                                ArrayList<String> uidMatches = (ArrayList<String>) document.getData().get("uidMatches");
                                ArrayList<String> uidMatches2 = (ArrayList<String>) document.getData().get("uidMatchRequests");

                                uidMatches.addAll(uidMatches2);
                                fetchCandidates(swipeActivity, profiles2, uidMatches); // Use the UIDs to fetch information of users
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void loadProfiles(DisplayChatsActivity displayChatsActivity, ArrayList<CardProfile> profiles2) {
        // The card area corresponds to the matches of the user.
        final boolean[] endMethod = {false};

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

        Log.d(SwipeActivity.firebaseLogKey, "Now attempting to get matched users...");
        // Fetch the UIDs of matched users (to know which ones to exclude in the swipe cards)
        firestore.collection("Matches")
                .whereEqualTo("uid", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> uidMatches = (ArrayList<String>) document.getData().get("uidMatches");

                                fetchCandidates(displayChatsActivity, profiles2, uidMatches); // Fetch pets
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getUserAnimal(){
        DocumentReference reference = firestore.collection("Pets").document(mAuth.getUid());

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                    currAnimal = documentSnapshot.getString("animal").toString();
                else
                    System.out.println("does not exist");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                System.out.println("cannot retrieve animal");
            }
        });
    }

    private void fetchCandidates(SwipeActivity swipeActivity, ArrayList<CardProfile> profiles2, ArrayList<String> uidMatches) {
        // For each candidate Pet, get their UID, exclude matched ones
        firestore.collection("Pets")
                .whereNotEqualTo("uid", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(SwipeActivity.firebaseLogKey, "Fetched all candidates!! Now adding to app...");
                        if (task.isSuccessful()) {
                            numberOfCandidates[0] = task.getResult().size();
                            if (numberOfCandidates[0] == 0) swipeActivity.showMessage(); // No available candiates.
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // For each candidate pet, fetch information about them
                                Log.d(SwipeActivity.firebaseLogKey, document.getId() + " => " + document.getData());
                                String candidateUid = document.getData().get("uid").toString();
                                String petName = document.getData().get("petName").toString();
                                String petDescription = document.getData().get("petDescription").toString();
                                String animalType = document.getData().get("animal").toString();

                                System.out.println("not swiped" + !uidMatches.contains(candidateUid));
                                // Add their information to the app, exclude them if matched
                                if (!uidMatches.contains(candidateUid) && animalType.equals(currAnimal)) {
                                    CardProfile tempCardProfile = new CardProfile(petName, petDescription, candidateUid);
                                    fetchImage(tempCardProfile, swipeActivity, profiles2);
                                }
                                else {
                                    numberOfCandidates[0]--;
                                }

                                if (numberOfCandidates[0] == 0) {
                                    swipeActivity.finalizeRecyclerView();
                                }

                                Log.d(SwipeActivity.firebaseLogKey, "Profile saved!");
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void fetchCandidates(DisplayChatsActivity displayChatsActivity, ArrayList<CardProfile> profiles2, ArrayList<String> uidMatches) {
        // For each candidate Pet, get their UID
        firestore.collection("Pets")
                .whereNotEqualTo("uid", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(SwipeActivity.firebaseLogKey, "Fetched all candidates!! Now adding to app...");
                        if (task.isSuccessful()) {
                            numberOfCandidates[0] = task.getResult().size();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // For each candidate pet, fetch information about them
                                Log.d(SwipeActivity.firebaseLogKey, document.getId() + " => " + document.getData());
                                String candidateUid = document.getData().get("uid").toString();
                                String petName = document.getData().get("petName").toString();
                                String petDescription = document.getData().get("petDescription").toString();; // TODO: Wait for them to add the edit texts on Registration. Then come back to this.

                                // Add their information to the app, only include those matched
                                if (uidMatches.contains(candidateUid)) {
                                    CardProfile tempCardProfile = new CardProfile(petName, petDescription, candidateUid);
                                    fetchImage(tempCardProfile, displayChatsActivity, profiles2);
                                }
                                else {
                                    numberOfCandidates[0]--;
                                }

                                Log.d(SwipeActivity.firebaseLogKey, "Profile saved!");
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Fetches the given image file from Cloud Storage.
     */
    private Bitmap fetchImage(CardProfile tempCardProfile, SwipeActivity swipeActivity, ArrayList<CardProfile> profiles2) {
        final Bitmap[] bitmap = new Bitmap[1];
        firestore.clearPersistence();

        // Get the filename of the candidate's profile picture
        firestore.collection("filenames")
                .whereEqualTo("uid", tempCardProfile.getUid())
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
                                        bitmap[0] = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                                        tempCardProfile.setUserProfilePicture(bitmap[0]);

                                        profiles2.add(tempCardProfile);
                                        Log.d(SwipeActivity.firebaseLogKey, "Saved profile picture!");

                                        numberOfCandidates[0]--;

                                        if (numberOfCandidates[0] == 0) {
                                            swipeActivity.finalizeRecyclerView();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        bitmap[0] = null;
                                        Log.d(SwipeActivity.firebaseLogKey, "Failed to fetch profile picture from Cloud Storage");
                                    }
                                });
                            }
                        } else {
                            bitmap[0] = null;
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return bitmap[0];
    }

    /**
     * Fetches the given image file from Cloud Storage.
     */
    private Bitmap fetchImage(CardProfile tempCardProfile, DisplayChatsActivity displayChatsActivity, ArrayList<CardProfile> profiles2) {
        final Bitmap[] bitmap = new Bitmap[1];
        firestore.clearPersistence();

        // Get the filename of the candidate's profile picture
        firestore.collection("filenames")
                .whereEqualTo("uid", tempCardProfile.getUid())
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
                                        bitmap[0] = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                                        tempCardProfile.setUserProfilePicture(bitmap[0]);

                                        profiles2.add(tempCardProfile);
                                        Log.d(SwipeActivity.firebaseLogKey, "Saved profile picture!");

                                        numberOfCandidates[0]--;

                                        if (numberOfCandidates[0] == 0) {
                                            displayChatsActivity.finalizeReyclerView();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        bitmap[0] = null;
                                        Log.d(SwipeActivity.firebaseLogKey, "Failed to fetch profile picture from Cloud Storage");
                                    }
                                });
                            }
                        } else {
                            bitmap[0] = null;
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return bitmap[0];
    }
}
