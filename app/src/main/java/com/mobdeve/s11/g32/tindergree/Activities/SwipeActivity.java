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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.Adapters.CardAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.CardDataHelper;
import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SwipeActivity extends AppCompatActivity {

    private CardStackView rv_cardarea;
    private CardStackLayoutManager manager;
    private CardAdapter cardAdapter;
    private ArrayList<Profile> profiles;
    private FloatingActionButton fab_openchats;
    private ImageButton ib_opensettings;
    private ImageButton ib_opennotifs;
    private ProgressBar pb_swipeActivity;
    private TextView tv_swipesysnotif;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

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

        // If not signed in, destroy this activity and redirect to Register activity
        if(currentUser != null){
            Log.d(firebaseLogKey, "User has signed in.");
            Log.d(firebaseLogKey, currentUser.getUid() + " UID.");
        }
        else {
            Log.d(firebaseLogKey, "User has NOT signed in.");
            Toast.makeText(SwipeActivity.this, "To bypass this, comment out codes " +
                    "at SwipeActivity starting at line 58.", Toast.LENGTH_LONG).show();
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

        this.tv_swipesysnotif.setVisibility(View.GONE);

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

        Log.d("MyApp","I am here");
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.initRecyclerView(); // Instantiate the Cards
    }

    public void initRecyclerView(){
        CardDataHelper carddataHelper = new CardDataHelper();

        System.out.println("pasok sa init");

        this.profiles = carddataHelper.loadProfileData();

        this.rv_cardarea = findViewById(R.id.cs_cardarea);

        this.manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    Toast.makeText(SwipeActivity.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                /*if (direction == Direction.Top){
                    Toast.makeText(MainActivity.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }*/
                if (direction == Direction.Left){
                    Toast.makeText(SwipeActivity.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }/*
                if (direction == Direction.Bottom){
                    Toast.makeText(MainActivity.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }*/


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

        this.rv_cardarea.setLayoutManager(this.manager);

        this.cardAdapter = new CardAdapter(this.profiles);
        this.rv_cardarea.setAdapter(this.cardAdapter);
    }
}