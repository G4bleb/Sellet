package com.uqac.sellet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import androidx.annotation.NonNull;

public class PictureLoader {
    private static final String TAG = "ProfileManager";
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    void setProfilePicture(final Context context, Uri imageUri){
        setPicture(context, imageUri, "profilepictures", mAuth.getCurrentUser().getUid());
    }

    void getProfilePicture(final Context context, ImageView imageView, String id){
        getPicture(context, imageView, "profilepictures/"+id+".png");
    }

    void setPicture(final Context context, Uri imageUri, String storageFolder, String name){
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        // Defining the child of storageReference
        StorageReference ref = storage.getReference().child(storageFolder+"/"+name+".png");

        // adding listeners on upload
        // or failure of image
        UploadTask uploadTask = ref.putBytes(data);
        Toast.makeText(context, "Uploading...", Toast.LENGTH_SHORT).show();
//        ref.putFile(imageUri).addOnSuccessListener(
        uploadTask.addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void getPicture(final Context context, ImageView imageView, String url){
        // Load the image using Glide
        Glide.with(context)
                .load(storage.getReference().child(url))
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }

}
