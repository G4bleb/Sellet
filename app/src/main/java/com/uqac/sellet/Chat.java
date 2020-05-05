package com.uqac.sellet;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class Chat {
    private static final String TAG = "Chat";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private CollectionReference chatsRef = db.collection("chats");

    private OnReadyListener readyListener;

    List<String> chatters;
    ArrayList<Message> messages = new ArrayList<>();
    CollectionReference messagesCollection;

    Chat(){

    }

    Chat(final String uidToChatTo){
        chatsRef.document(idFromUidToChatTo(uidToChatTo))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Chat " + document.getId() + " found");
                                Chat.this.messagesCollection = document.getReference().collection("messages");
                                Chat.this.chatters = (List<String>) document.get("chatters");
                                getMessagesFromMessagesReference();
                            } else {
                                Log.d(TAG, "No such document");
                                createChat(uidToChatTo);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    void createChat(String uidToChatTo){
        Map<String, Object> chat = new HashMap<>();
        chat.put("chatters", Arrays.asList(mAuth.getCurrentUser().getUid(), uidToChatTo));

        chatsRef.document(idFromUidToChatTo(uidToChatTo)).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                }
            });
    }

    void getMessagesFromMessagesReference() {
        messagesCollection.orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Chat.this.messages.add(new Message(
                                            document.getString("author"),
                                            document.getTimestamp("timestamp"),
                                            document.getString("content")
                                    ));
                                } else {
                                    Log.d(TAG, "Document does not exist");
                                }
                            }
                            if (readyListener != null) {
                                readyListener.onReady(Chat.this);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    Chat setOnReadyListener(OnReadyListener rl){
        readyListener = rl;
        return this;
    }

    String idFromUidToChatTo(String uidToChatTo){
        ArrayList<String> chattersList = new ArrayList<>();
        chattersList.add(mAuth.getCurrentUser().getUid());
        chattersList.add(uidToChatTo);

        Collections.sort(chattersList);

        StringBuilder sb = new StringBuilder();
        for (String s : chattersList) {
            sb.append(s);
        }
        return sb.toString();
    }

    Chat allMyContacts() {
        final ArrayList<String> mycontacts = new ArrayList<>();
        chatsRef.whereArrayContains("chatters", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                List<String> chatters = (List<String>) document.get("chatters");
                                for(String chatter : chatters){
                                    if(!chatter.equals(mAuth.getCurrentUser().getUid())){
                                        mycontacts.add(chatter);
                                        if (readyListener != null) {
                                            readyListener.onReady(mycontacts);
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return this;
    }

    Chat addMessage(String content) {
        final Message msg = new Message(mAuth.getCurrentUser().getUid(), Timestamp.now(), content);
        messagesCollection.add(msg.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        if (readyListener != null) {
                            readyListener.onReady(msg);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        return this;
    }

}
