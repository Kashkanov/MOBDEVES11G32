package com.mobdeve.s11.g32.tindergree.DataHelpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.Activities.ChatActivity;
import com.mobdeve.s11.g32.tindergree.Activities.SwipeActivity;
import com.mobdeve.s11.g32.tindergree.Models.Chat;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ChatDataHelper {
    private FirebaseDatabase database;

    public ChatDataHelper() {
        database = FirebaseDatabase.getInstance("https://tindergree-default-rtdb.asia-southeast1.firebasedatabase.app"); // TODO: Don't forget to change the parameters if switching between emulator / production.

        // Comment these lines if production Firebase should be used instead of emulator
        if (SwipeActivity.useEmulator) {
            try {
                database = FirebaseDatabase.getInstance();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.useEmulator("10.0.2.2", 9000);
            } catch (IllegalStateException e) {
                Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
            }
        }
    }

    // For testing purposes
    public void testLoadMessages(ChatActivity chatActivity, ArrayList<Chat> messages, ChatActivity activity) {
        messages.add(new Chat("Hello po!", "UID HERE"));
        messages.add(new Chat("Hello Again!", "UID HERE"));
        activity.finalizeChatRecyclerView();
    }

    public void loadMessages(ChatActivity chatActivity, ArrayList<Chat> messages, String chatUid) {
        DatabaseReference chatUIDRef = database.getReference("ChatMessages");
        Log.d(SwipeActivity.firebaseLogKey, "Loading chat from ChatUID: " + chatUid);

        // TODO: Continue from here. Test out handling of empty retrieval.
        chatUIDRef.child(chatUid)
                .orderByKey()
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e(SwipeActivity.firebaseLogKey, "Error getting data", task.getException());
                }
                else {
                    Log.d(SwipeActivity.firebaseLogKey, String.valueOf("Got results! " + String.valueOf(task.getResult().getValue())));
                    if (task.getResult().exists() == false) { // No chat history
                        Log.d(SwipeActivity.firebaseLogKey, "No chat history.");
                        return;
                    }

                    Map<String, Object> chatEntriesTemp = (Map<String, Object>) task.getResult().getValue();
                    Map <String, Object> chatEntries = new TreeMap<>(chatEntriesTemp);

                    // Retrieve each message
                    for (Map.Entry<String, Object> chatEntry : chatEntries.entrySet()) { // -MiMTkAGO_bqnMlbhemc={sender=UID HERE, message=Hello po!}
                        Map<String, String> chatEntryMessageDetail = (Map<String, String>) chatEntry.getValue(); // {sender=UID HERE, message=Hello po!}

                        String messageId = chatEntry.getKey();
                        String message = chatEntryMessageDetail.get("message");
                        String sender = chatEntryMessageDetail.get("sender");

                        chatActivity.messageIDs.add(messageId);
                        messages.add(new Chat(message, sender));
                    }

                    chatActivity.finalizeChatRecyclerView();
                    chatActivity.registerMessageListener();
                }
            }
        });
    }
}
