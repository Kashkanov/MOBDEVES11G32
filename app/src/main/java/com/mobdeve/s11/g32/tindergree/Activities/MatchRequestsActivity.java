package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.Adapters.MatchRequestAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.MatchRequestHelper;
import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.Models.Matches;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class MatchRequestsActivity extends AppCompatActivity {

    private RecyclerView rv_matchrequestarea;
    public ArrayList<MatchRequest> matchRequests; // Populate this for the recycler view
    public Matches matches; // For updating the database
    private MatchRequestAdapter matchRequestAdapter;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_requests);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.clearPersistence();

        // Comment these lines if production Firebase should be used instead of emulator
        try {
            FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
            firestore.useEmulator("10.0.2.2", 8080);
        }
        catch (IllegalStateException e) {
            Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
        }

        this.matchRequests = new ArrayList<MatchRequest>();
        this.initrecyclerview();
    }

    public void initrecyclerview(){
        this.rv_matchrequestarea = findViewById(R.id.rv_matchrequestarea);
        MatchRequestHelper matchRequestHelper = new MatchRequestHelper();
        matchRequestHelper.loadMatchRequestData(this);
    }

    public void finalizeRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, GridLayoutManager.VERTICAL,false);

        this.rv_matchrequestarea.setLayoutManager(manager);
        this.matchRequestAdapter = new MatchRequestAdapter(this.matchRequests, this);

        this.rv_matchrequestarea.setAdapter(this.matchRequestAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

        matches.removeMatchRequests(uidRequest);
        matches.addMatches(uidRequest);

        // Update the database
        firestore.collection("Matches").document(mAuth.getUid()).set(matches);

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

}