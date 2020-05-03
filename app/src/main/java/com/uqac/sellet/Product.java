package com.uqac.sellet;

import android.content.Context;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class Product {
    private static final String TAG = "Product";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Context context;

    String id = null;
    String name = "";
    String desc = "";
    String owner = null;
    double price = 0d;
    List<String> picturesLinks = new ArrayList<String>();

    ArrayList<Uri> picturesArray = new ArrayList<Uri>();

    private OnReadyListener readyListener;

    public Product(Context context, String name, String desc, double price){
        this.context = context;
        owner = mAuth.getCurrentUser().getUid();
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    Product(Context context, String id){
        this.context = context;
        this.id = id;
    }

//    Product(Context context){
//        this.context = context;
//    }

    public void publish(){
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("desc", desc);
        product.put("owner", owner);
        product.put("price", price);
        product.put("pictures", picturesLinks);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(context, "Product published", Toast.LENGTH_SHORT).show();
                        if(!picturesArray.isEmpty()){
                            Toast.makeText(context, "Uploading pictures...", Toast.LENGTH_SHORT).show();
                            PictureLoader pl = new PictureLoader();
                            int i = 1;
                            for (Uri picture: picturesArray) {
                                pl.setPicture(context, picture, "products/"+documentReference.getId(), Integer.toString(i++));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    Product get(){
        DocumentReference docRef = db.collection("products").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Product.this.name = document.getString("name");
                        Product.this.desc = document.getString("desc");
                        Product.this.owner = document.getString("owner");
                        Product.this.price = document.getDouble("price");
                        Product.this.picturesLinks = (List<String>) document.get("pictures");

                        if(readyListener != null){
                            readyListener.onReady(Product.this);
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

    Product setOnReadyListener(OnReadyListener rl){
        readyListener = rl;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"+id+", "+name+", "+desc+", "+owner+", "+price+", "+ picturesLinks +"} ("+picturesArray+")";
    }

}
