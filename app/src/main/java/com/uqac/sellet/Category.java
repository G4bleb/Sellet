package com.uqac.sellet;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uqac.sellet.entities.OnReadyListener;

public class Category {
    private static final String TAG = "Product";

    String id;
    String name;
    String iconLink;

    private Context context;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private OnReadyListener readyListener;

    public Category(Context ctx, String id, String name, String iconLink){
        this.context = ctx;
        this.id = id;
        this.name = name;
        this.iconLink = iconLink;
    }

    public Category(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"+id+", "+name+", "+iconLink+"}";
    }

    public Category getAllCategories(){
        ArrayList<Category> categories = new ArrayList<>();

        CollectionReference colRef = db.collection("categories");

        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Category c = new Category(Category.this.context);
                                c.id = document.getId();
                                c.name = document.getString("name");
                                c.iconLink = document.getString("icon");

                                categories.add(c);
                            }
                            if(readyListener != null){
                                readyListener.onReady(categories);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return this;
    }

    Map<String, Object> toMap(){
        Map<String, Object> category = new HashMap<>();
        category.put("name", name);
        category.put("icon", iconLink);
        return category;
    }

    public Category setOnReadyListener(OnReadyListener rl){
        readyListener = rl;
        return this;
    }

}
