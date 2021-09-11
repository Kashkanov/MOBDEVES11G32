package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import com.mobdeve.s11.g32.tindergree.DataHelpers.Keys;
import com.mobdeve.s11.g32.tindergree.Models.Chat;
import com.mobdeve.s11.g32.tindergree.Models.ChatUid;
import com.mobdeve.s11.g32.tindergree.R;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ShapeableImageView ibKaChatPic;
    private Toolbar toolbar;
    private TextView tvKaChatName;
    private TextView tvKaChatDesc;
    private EditText etMessage;
    private RecyclerView rvChat;
    private ImageButton btnSendMessage;
    public static final String KEY_KACHATNAME = "KEY_KACHATNAME";
    public static final String KEY_KACHATPIC = "KEY_KACHATPIC";
    public static final String KEY_KACHATDESC = "KEY_KACHATDESC";
    public static final String KEY_UID = "KEY_UID";
    private String kachatuid;

    public Boolean readyToListenToIncomingMessage;

    private ArrayList<Chat> messages;
    private String chatUid;
    private SharedPreferences sp;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_TindergreeDark);
        }
        else{
            setTheme(R.style.Theme_Tindergree);
        }
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);

        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

        this.ibKaChatPic = findViewById(R.id.ib_kachatpic);
        this.tvKaChatName = findViewById(R.id.tv_kachatname);
        this.tvKaChatDesc = findViewById(R.id.tv_kachatdesc);
        this.etMessage = findViewById(R.id.et_message);
        this.btnSendMessage = findViewById(R.id.btn_sendmessage);
        this.toolbar = findViewById(R.id.chat_toolbar);


        Intent i = getIntent();
        String kachatname =i.getStringExtra(MatchAdapter.KEY_MATCHNAME);
        this.tvKaChatName.setText(kachatname);
        String kachatdesc = i.getStringExtra(MatchAdapter.KEY_MATCHDESC);
        this.tvKaChatDesc.setText(kachatdesc);

        this.kachatuid = i.getStringExtra(MatchAdapter.KEY_UID);
        this.chatUid = getChatUid();

        this.ibKaChatPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), OtherPicsActivity.class);
                i.putExtra(CardAdapter.KEY_PROFNAME, kachatname);
                i.putExtra(CardAdapter.KEY_PROFDESC, kachatdesc);
                i.putExtra(CardAdapter.KEY_PROFUID, kachatuid);
                v.getContext().startActivity(i);
            }
        });

        this.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,DisplayChatsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        messages = new ArrayList<>();

        fetchProfilePicture();
        getChatUid();
    }

    @Override
    protected void onResume() {
        super.onResume();

        readyToListenToIncomingMessage = false;
        loadDayNight();
        loadChatting();
        registerMessageListener();
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
                                        ibKaChatPic.setImageBitmap(bitmap);
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

    public void finalizeChatRecyclerView() {
        this.rvChat = findViewById(R.id.rv_chat);
        LinearLayoutManager manager = new LinearLayoutManager(this, GridLayoutManager.VERTICAL,false);

        this.rvChat.setLayoutManager(manager);
        this.rvChat.setAdapter(new ChatAdapter(messages, mAuth.getUid(), tvKaChatName.getText().toString()));
    }

    private void loadDayNight(){
        Boolean darkBool = this.sp.getBoolean(Keys.KEY_DARK_BOOL.name(), false);
        if(darkBool) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
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
        new ChatDataHelper().loadMessages(this, this.messages, this.chatUid);
    }

    /**
     * Registers the callback for new incoming messages.
     * Allows the front-end to be updated in real-time.
     */
    private void registerMessageListener() {
        // If incoming message is from other user, add to view.
        if (readyToListenToIncomingMessage == false)
            return;

        DatabaseReference chatMessagesRef = database.getReference("ChatMessages").child(chatUid);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Log.d(SwipeActivity.firebaseLogKey, "onChildAdded:" + snapshot.getKey());

                String key = snapshot.getKey();
                Map<String, Object> chatEntry = (Map<String, Object>) snapshot.getValue();

                String message = (String) chatEntry.get("message");
                String sender = (String) chatEntry.get("sender");

                Chat newMessage = new Chat(message, sender);
                if (newMessage.getSender().compareTo(mAuth.getUid()) != 0) { // New message from other user
                    Log.d(SwipeActivity.firebaseLogKey, "New incoming message!");
                    messages.add(newMessage);
                    if (messages.size() == 1) {
                        finalizeChatRecyclerView();
                    }
                    else {
                        rvChat.getAdapter().notifyItemChanged(messages.size());
                        rvChat.getAdapter().notifyItemRangeChanged(messages.size(), messages.size());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Log.d(SwipeActivity.firebaseLogKey, "onChildChanged:" + snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                // Something
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                // Something
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // Something
            }
        };

        chatMessagesRef.addChildEventListener(childEventListener);
    }

    private void sendMessage() {
        this.rvChat = findViewById(R.id.rv_chat);
        DatabaseReference chatMessagesRef = database.getReference("ChatMessages");

        Log.d(SwipeActivity.firebaseLogKey, "Saving message to ChatUID: " + this.chatUid);

        String message = this.etMessage.getText().toString();
        if (message.length() == 0) {
            this.etMessage.setError("Do not leave blank!");
            return;
        }

        Chat messageEntry = new Chat(message, mAuth.getUid());

        chatMessagesRef.child(this.chatUid).push().setValue(messageEntry);

        this.messages.add(messageEntry);
        if (this.messages.size() == 1) {
            finalizeChatRecyclerView();
        }
        else {
            this.rvChat.getAdapter().notifyItemChanged(this.messages.size());
            this.rvChat.getAdapter().notifyItemRangeChanged(this.messages.size(), this.messages.size());
        }

        this.etMessage.setText("");
    }
}

