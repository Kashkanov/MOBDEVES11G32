package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g32.tindergree.Adapters.CardAdapter;
import com.mobdeve.s11.g32.tindergree.Adapters.ChatAdapter;
import com.mobdeve.s11.g32.tindergree.Adapters.MatchAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.ChatDataHelper;
import com.mobdeve.s11.g32.tindergree.Models.Chat;
import com.mobdeve.s11.g32.tindergree.Models.ChatUid;
import com.mobdeve.s11.g32.tindergree.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageButton ib_kachatpic;
    private TextView tv_kachatname;
    private TextView tv_kachatdesc;
    private RecyclerView rv_chat;
    public static final String KEY_KACHATNAME = "KEY_KACHATNAME";
    public static final String KEY_KACHATPIC = "KEY_KACHATPIC";
    public static final String KEY_KACHATDESC = "KEY_KACHATDESC";
    public static final String KEY_UID = "KEY_UID";
    private String kachatuid;

    private ArrayList<Chat> messages;
    private String chatUid;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);

        changeStatusBarColor();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance(); // TODO: Don't forget to change the parameters if switching between emulator / production.

        // Comment these lines if production Firebase should be used instead of emulator
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.useEmulator("10.0.2.2", 9000);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
            FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
            firestore.useEmulator("10.0.2.2", 8080);
        }
        catch (IllegalStateException e) {
            Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
        }

        this.ib_kachatpic = findViewById(R.id.ib_kachatpic);
        this.tv_kachatname = findViewById(R.id.tv_kachatname);
        this.tv_kachatdesc = findViewById(R.id.tv_kachatdesc);

        Intent i = getIntent();
        String kachatname =i.getStringExtra(MatchAdapter.KEY_MATCHNAME);
        this.tv_kachatname.setText(kachatname);
        String kachatdesc = i.getStringExtra(MatchAdapter.KEY_MATCHDESC);
        this.tv_kachatdesc.setText(kachatdesc);

        this.kachatuid = i.getStringExtra(MatchAdapter.KEY_UID);
        this.chatUid = getChatUid();

        this.ib_kachatpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), OtherPicsActivity.class);
                i.putExtra(CardAdapter.KEY_PROFNAME, kachatname);
                i.putExtra(CardAdapter.KEY_PROFDESC, kachatdesc);
                i.putExtra(CardAdapter.KEY_PROFUID, kachatuid);
                v.getContext().startActivity(i);
            }
        });

        messages = new ArrayList<>();

        fetchProfilePicture();
        loadChatting();
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    private void fetchProfilePicture() {
        StorageReference storageRef = storage.getReference();

        firestore.collection("filenames")
                .whereEqualTo("uid", this.kachatuid)
                .whereEqualTo("isProfilePicture", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String filename = document.getData().get("filename").toString();

                                // Got the filename, now fetch the image file from storage
                                StorageReference profilePicRef = storageRef.child("Users/" + kachatuid + "/" + filename);

                                final long MAX_BYE_SIZE = 1024 * 10024;
                                profilePicRef.getBytes(MAX_BYE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // Show the profile picture to the app
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                                        ib_kachatpic.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Failed to get profile picture");
                        }
                    }
                });
    }

    private void initializeChatRecyclerView() {
        this.rv_chat = findViewById(R.id.rv_chat);
    }

    public void finalizeChatRecyclerView() {
        this.rv_chat = findViewById(R.id.rv_chat);
        LinearLayoutManager manager = new LinearLayoutManager(this, GridLayoutManager.VERTICAL,false);

        this.rv_chat.setLayoutManager(manager);
        this.rv_chat.setAdapter(new ChatAdapter(messages));
    }

    private String getChatUid() {
        String toHash; // Concatentation of two UIDs to hash
        String chatUID; // Hashed Chat ID

        if (mAuth.getCurrentUser().getUid().compareTo(kachatuid) < 0) {
            toHash = mAuth.getCurrentUser().getUid().concat(kachatuid);
        }
        else {
            toHash = kachatuid.concat(mAuth.getCurrentUser().getUid());
        }

        chatUID = String.valueOf(toHash.hashCode());

        return chatUID;
    }

    // TODO: implement chatting feature
    // Compare two UIDs, combine, get hash
    // Set hash as Chat UID on realtime database
    // Use that Chat UID to retrieve messages
    // Save messages using push()
    private void loadChatting() {
        Log.d(SwipeActivity.firebaseLogKey, "Loading chat messages...");

        // Load the chat messages from Realtime Database using the Chat UID
        new ChatDataHelper().testLoadMessages(this, this.messages, this);
        // new ChatDataHelper().loadMessages(this, this.messages, this.chatUid);
    }

    /**
     * Registers the callback for new incoming messages.
     * Allows the front-end to be updated in real-time.
     */
    private void registerMessageListener() {

    }

    private void sendMessage() {
        // TODO: Continue here (Register onclick event to the Send button first) (use test chat data)

        //Chat testChat = new Chat("AAAAAAAA!", "UID HERE");
        //myRef.push().setValue(testChat);
    }
}

