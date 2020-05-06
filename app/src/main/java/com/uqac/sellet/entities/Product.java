package com.uqac.sellet.entities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uqac.sellet.R;

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

    public String id = null;
    public String name = "";
    public String desc = "";
    public String owner = null;
    public double price = 0d;
    public List<String> picturesLinks = new ArrayList<String>();

    public ArrayList<Uri> picturesArray = new ArrayList<Uri>();

    private OnReadyListener readyListener;

    public Product(Context context, String name, String desc, double price){
        this.context = context;
        owner = mAuth.getCurrentUser().getUid();
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public Product(Context context, String id){
        this.context = context;
        this.id = id;
    }

    public Product(Context context){
        this.context = context;
    }

    public void publish(){
        owner = mAuth.getCurrentUser().getUid();
        db.collection("products")
                .add(this.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(context, R.string.product_published, Toast.LENGTH_SHORT).show();
                        if(!picturesArray.isEmpty()){
                            Toast.makeText(context, R.string.uploading_pictures, Toast.LENGTH_SHORT).show();
                            PictureLoader pl = new PictureLoader();
                            int i = 1;
                            for (Uri picture: picturesArray) {
                                pl.setPicture(context, picture, "products/"+documentReference.getId(), Integer.toString(i++));
                            }
                        }
                        db.collection("users").document(mAuth.getCurrentUser().getUid()).update("products", FieldValue.arrayUnion(documentReference.getId()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    public Product get(){
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

    public Product getAllProducts(){
        ArrayList<Product> products = new ArrayList<>();

        CollectionReference colRef = db.collection("products");

        colRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            Product p = new Product(Product.this.context);
                            p.id = document.getId();
                            p.name = document.getString("name");
                            p.desc = document.getString("desc");
                            p.owner = document.getString("owner");
                            p.price = document.getDouble("price");
                            p.picturesLinks = (List<String>) document.get("pictures");

                            products.add(p);
                        }
                        if(readyListener != null){
                            readyListener.onReady(products);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        return this;
    }

    public Product setOnReadyListener(OnReadyListener rl){
        readyListener = rl;
        return this;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("desc", desc);
        product.put("owner", owner);
        product.put("price", price);
        product.put("pictures", picturesLinks);
        return product;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"+id+", "+name+", "+desc+", "+owner+", "+price+", "+ picturesLinks +"} ("+picturesArray+")";
    }

}
