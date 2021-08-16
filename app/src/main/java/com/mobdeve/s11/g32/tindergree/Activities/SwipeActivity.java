package com.mobdeve.s11.g32.tindergree.Activities;

//TODO: collapsible rv of otherpics
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s11.g32.tindergree.Adapters.CardAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.CardDataHelper;
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
    private ImageButton ib_openchats;
    private ImageButton ib_opensettings;

    private FirebaseAuth mAuth;

    public static String firebaseLogKey = "AUTH_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        // Use Firebase emulator instead
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // If not signed in, destroy this activity and redirect to Register activity
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Log.d(firebaseLogKey, "User has signed in.");
            Log.d(firebaseLogKey, currentUser.toString());
        }
        else {
            Log.d(firebaseLogKey, "User has NOT signed in.");
            Toast.makeText(SwipeActivity.this, "To bypass this, comment out codes " +
                    "at SwipeActivity starting at line 58.", Toast.LENGTH_LONG).show();
            Intent authenticateIntent = new Intent(SwipeActivity.this, RegisterActivity.class);
            startActivity(authenticateIntent);

            finish(); // Destroy activity
        }

        this.ib_openchats = findViewById(R.id.ib_openchats);
        this.ib_opensettings = findViewById(R.id.ib_opensettings);

        this.ib_openchats.setOnClickListener(new View.OnClickListener() {
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

        Log.d("MyApp","I am here");
        this.initRecyclerView();

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