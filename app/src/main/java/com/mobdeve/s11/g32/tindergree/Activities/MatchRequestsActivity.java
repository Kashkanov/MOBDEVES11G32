package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.Adapters.MatchRequestAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.Keys;
import com.mobdeve.s11.g32.tindergree.DataHelpers.MatchRequestHelper;
import com.mobdeve.s11.g32.tindergree.Models.ChatUid;
import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.Models.Matches;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class MatchRequestsActivity extends AppCompatActivity {

    private RecyclerView rvMatchRequestsArea;
    public ArrayList<MatchRequest> matchRequests; // Populate this for the recycler view
    public Matches matches, senderMatches; // For updating the database
    private MatchRequestAdapter matchRequestAdapter;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseDatabase database;

    private SharedPreferences sp;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_TindergreeDark);
        }
        else{
            setTheme(R.style.Theme_Tindergree);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_requests);
        changeStatusBarColor();

        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loadDayNight();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance("https://tindergree-default-rtdb.asia-southeast1.firebasedatabase.app"); // TODO: Don't forget to change the parameters if switching between emulator / production.

        firestore.clearPersistence();

        // Comment these lines if production Firebase should be used instead of emulator
        if (SwipeActivity.useEmulator) {
            try {
                database = FirebaseDatabase.getInstance();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.useEmulator("10.0.2.2", 9000);
                FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
                FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
                firestore.useEmulator("10.0.2.2", 8080);
            } catch (IllegalStateException e) {
                Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
            }
        }

        this.matchRequests = new ArrayList<MatchRequest>();
        this.initrecyclerview();

        toolbar = findViewById(R.id.match_requests_toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchRequestsActivity.this,SwipeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void loadDayNight(){
        Boolean darkBool = this.sp.getBoolean(Keys.KEY_DARK_BOOL.name(), false);
        if(darkBool) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    public void initrecyclerview(){
        this.rvMatchRequestsArea = findViewById(R.id.rv_matchrequestarea);
        MatchRequestHelper matchRequestHelper = new MatchRequestHelper();
        matchRequestHelper.loadMatchRequestData(this);
    }

    public void finalizeRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, GridLayoutManager.VERTICAL,false);

        this.rvMatchRequestsArea.setLayoutManager(manager)  ;
        this.matchRequestAdapter = new MatchRequestAdapter(this.matchRequests, this);

        this.rvMatchRequestsArea.setAdapter(this.matchRequestAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.loadDayNight();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void acceptRequest(String uidRequest) {
        // Find the item and remove it from view
        int i;
        for (i = 0; i < matchRequests.size() && !(matchRequests.get(i).getProfile().getUid() == uidRequest); i++);

        matchRequests.remove(i);

        // Update the database (match)
        matches.setUid(mAuth.getUid());
        matches.removeMatchRequests(uidRequest);
        matches.addMatches(uidRequest);
        firestore.collection("Matches").document(mAuth.getUid()).set(matches);

        updateSenderMatch(uidRequest);

        // Update the database (create Chat UID)
        ChatUid chatUid = new ChatUid();
        String chatUID = getChatUID(uidRequest, chatUid);
        DatabaseReference chatUidRef = database.getReference("ChatUID");
        chatUidRef.child(chatUID).setValue(chatUid);

        Log.d(SwipeActivity.firebaseLogKey, "Created Chat UID in Realtime Database.");

        this.matchRequestAdapter.notifyItemRemoved(i);
        this.matchRequestAdapter.notifyItemRangeChanged(i, matchRequests.size());
    }

    public void rejectRequest(String uidRequest) {
        int i;
        for (i = 0; i < matchRequests.size() && !(matchRequests.get(i).getProfile().getUid() == uidRequest); i++);

        matchRequests.remove(i);

        matches.removeMatchRequests(uidRequest);

        firestore.collection("Matches").document(mAuth.getUid()).set(matches);

        this.matchRequestAdapter.notifyItemRemoved(i);
        this.matchRequestAdapter.notifyItemRangeChanged(i, matchRequests.size());
    }

    private String getChatUID(String uidRequest, ChatUid chatUid) {
        String toHash; // Concatentation of two UIDs to hash
        String chatUID; // Hashed Chat ID

        if (mAuth.getCurrentUser().getUid().compareTo(uidRequest) < 0) {
            toHash = mAuth.getCurrentUser().getUid().concat(uidRequest);
            chatUid.setMember1(mAuth.getCurrentUser().getUid());
            chatUid.setMember2(uidRequest);
        }
        else {
            toHash = uidRequest.concat(mAuth.getCurrentUser().getUid());
            chatUid.setMember1(uidRequest);
            chatUid.setMember2(mAuth.getCurrentUser().getUid());
        }

        chatUID = String.valueOf(toHash.hashCode());
        chatUid.setChatUID(chatUID);

        return chatUID;
    }

    private void updateSenderMatch(String uidRequest) {
        firestore.collection("Matches")
                .whereEqualTo("uid", uidRequest)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uid = document.getData().get("uid").toString();
                                ArrayList<String> uidMatches = (ArrayList<String>) document.getData().get("uidMatches");
                                ArrayList<String> uidMatchRequests = (ArrayList<String>) document.getData().get("uidMatchRequests");
                                uidMatches.add(mAuth.getUid());

                                senderMatches = new Matches(uid, uidMatches, uidMatchRequests);

                                firestore.collection("Matches").document(uid).set(senderMatches);

                                Log.d(SwipeActivity.firebaseLogKey, "Updated sender matches firestore record!");
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

}