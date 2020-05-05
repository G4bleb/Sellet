package com.uqac.sellet;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;

public class User {
    private static final String TAG = "User";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Context context;

    String uid = null;
    double grade = 0d;
    double graders = 0d;
    String name = "";
    List<String> products;

    private OnReadyListener readyListener;

//    User(Context context){
//        this.context = context;
//    }

    User(Context context, String uid){
        this.context = context;
        this.uid = uid;
        get();
    }

    User get(){
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        User.this.grade = document.getDouble("grade");
                        User.this.graders = document.getDouble("graders");
                        User.this.name = document.getString("name");
                        User.this.products = (List<String>) document.get("products");
                        if(readyListener != null){
                            readyListener.onReady(User.this);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return this;
    }

    void setUsername(String newusername){
        DocumentReference ref = db.collection("users").document(mAuth.getCurrentUser().getUid());

        ref.update("name", newusername)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(context, "Name updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    User setOnReadyListener(OnReadyListener rl){
        readyListener = rl;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"+uid+", "+name+", "+grade+", "+graders+", "+products+"}";
    }
}
