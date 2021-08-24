package com.mobdeve.s11.g32.tindergree.Activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.Adapters.CardAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.CardDataHelper;
import com.mobdeve.s11.g32.tindergree.Models.CardProfile;
import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.Models.Matches;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SwipeActivity extends AppCompatActivity {

    private CardStackView rv_cardarea;
    private CardStackLayoutManager manager;
    private CardAdapter cardAdapter;
    private FloatingActionButton fab_openchats;
    private ImageButton ib_opensettings;
    private ImageButton ib_opennotifs;
    private ImageView iv_notifdot;
    private ProgressBar pb_swipeActivity;
    private TextView tv_swipesysnotif;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private ArrayList<CardProfile> profiles2 = new ArrayList<>();

    public static String firebaseLogKey = "AUTH_TEST";

    /**
     * BroadcastReceiver to end this activity from another activity.
     * This only happens if the user is attempting to log out from the Settings.
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish_main_activity")) {
                finish();
                Log.d(SwipeActivity.firebaseLogKey, "SwipeActivity ended.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        registerReceiver(broadcastReceiver, new IntentFilter("finish_main_activity"));

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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        firestore.clearPersistence();

        // If not signed in, destroy this activity and redirect to Register activity
        if(currentUser != null){
            Log.d(firebaseLogKey, "User has signed in.");
            Log.d(firebaseLogKey, currentUser.getUid() + " UID.");
        }
        else {
            Log.d(firebaseLogKey, "User has NOT signed in.");
            Intent authenticateIntent = new Intent(SwipeActivity.this, RegisterActivity.class);
            startActivity(authenticateIntent);

            finish(); // Destroy activity
            return;
        }

        this.fab_openchats = findViewById(R.id.fab_openchats);
        this.ib_opensettings = findViewById(R.id.ib_opensettings);
        this.ib_opennotifs = findViewById(R.id.ib_opennotifs);
        this.pb_swipeActivity = findViewById(R.id.pb_swipeActivity);
        this.tv_swipesysnotif = findViewById(R.id.tv_swipesysnotif);
        this.iv_notifdot = findViewById(R.id.iv_notifdot);

        this.tv_swipesysnotif.setVisibility(View.GONE);
        iv_notifdot.setVisibility(View.INVISIBLE);

        this.fab_openchats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DisplayChatsActivity.class);

                v.getContext().startActivity(i);
            }
        });

        this.ib_opensettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SettingsActivity.class);

                v.getContext().startActivity(i);
            }
        });

        this.ib_opennotifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MatchRequestsActivity.class);

                v.getContext().startActivity(i);
            }
        });

        this.initRecyclerView(); // Instantiate the Cards
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMatchRequests();
    }

    public void initRecyclerView(){
        this.rv_cardarea = findViewById(R.id.cs_cardarea);

        rv_cardarea.setVisibility(View.GONE);
        pb_swipeActivity.setVisibility(View.VISIBLE);
        tv_swipesysnotif.setVisibility(View.GONE); // Show this error message view if no available profiles are fetched.

        CardDataHelper carddataHelper = new CardDataHelper();

        Log.d(SwipeActivity.firebaseLogKey, "Loading cards...");
        carddataHelper.loadProfiles(this, profiles2);

        Log.d(SwipeActivity.firebaseLogKey, "Loading matches...");

    }

    /**
     * Call when there are no available matches.
     */
    public void showMessage() {
        rv_cardarea.setVisibility(View.GONE);
        pb_swipeActivity.setVisibility(View.GONE);
        tv_swipesysnotif.setVisibility(View.VISIBLE); // Show this error message view if no available profiles are fetched.
    }

    public void finalizeRecyclerView() {
        Log.d(SwipeActivity.firebaseLogKey, "All candidates have been fetched! Setting up the RecyclerView now...");

        // Setup the CardStackLayoutManager
        this.manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);

                if (direction == Direction.Right){
                    sendMatchRequest(profiles2.get(0).getUid());

                    profiles2.remove(0);
                    Toast.makeText(SwipeActivity.this, "Direction Right: Sent request.", Toast.LENGTH_SHORT).show();

                    cardAdapter.notifyItemRemoved(0);
                    cardAdapter.notifyItemRangeChanged(0, profiles2.size());
                }

                if (direction == Direction.Left){
                    profiles2.remove(0);
                    Toast.makeText(SwipeActivity.this, "Direction Left: Ignore match.", Toast.LENGTH_SHORT).show();

                    cardAdapter.notifyItemRemoved(0);
                    cardAdapter.notifyItemRangeChanged(0, profiles2.size());
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.tv_petname);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.tv_petname);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());

        // Assign the CardStackLayoutManager to the RecyclerView
        this.rv_cardarea.setLayoutManager(this.manager);

        // Load the data onto the CardAdapter
        this.cardAdapter = new CardAdapter(this.profiles2);

        // Set the RecyclerView's adapter
        this.rv_cardarea.setAdapter(this.cardAdapter);

        rv_cardarea.setVisibility(View.VISIBLE);
        pb_swipeActivity.setVisibility(View.GONE);
        tv_swipesysnotif.setVisibility(View.GONE);
    }

    private void fetchMatchRequests() {
        firestore.collection("Matches")
                .whereEqualTo("uid", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(SwipeActivity.firebaseLogKey, "MATCH data:" + " => " + document.getData());
                                ArrayList<String> uidMatches = (ArrayList<String>) document.getData().get("uidMatchRequests");

                                if (uidMatches.size() == 0) {
                                    iv_notifdot.setVisibility(View.INVISIBLE);
                                    Log.d(SwipeActivity.firebaseLogKey, "No match requests available.");
                                }
                                else {
                                    iv_notifdot.setVisibility(View.VISIBLE);
                                    Log.d(SwipeActivity.firebaseLogKey, "Match requests available.");
                                }
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void sendMatchRequest(String receipientUid) {
        DocumentReference matchesRequestsRef = firestore.collection("Matches").document(receipientUid);

        // Get the matches data and save it
        firestore.collection("Matches")
                .whereEqualTo("uid", receipientUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map fetchedMatchRecord = document.getData();
                                ArrayList<String> uidMatches = (ArrayList<String>) document.getData().get("uidMatches");
                                ArrayList<String> uidMatchRequests = (ArrayList<String>) document.getData().get("uidMatchRequests");

                                Matches matches = new Matches(receipientUid, uidMatches, uidMatchRequests);

                                matches.addMatchRequest(mAuth.getUid());

                                // Update database
                                firestore.collection("Matches").document(receipientUid).set(matches);
                                Log.d(SwipeActivity.firebaseLogKey, "Sent match request!");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}