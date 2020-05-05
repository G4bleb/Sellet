package com.uqac.sellet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private static final int PICK_PROFILE_PICTURE = 1;
    private static final int PICK_PRODUCT_PICTURE = 2;

    private TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        testTextView = findViewById(R.id.testtextview);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        if(currentUser == null){
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
        }

    }

    public void getProduct(View view){
        Product p = new Product(this, "7Rl8jITR5oH910MokNgG");
        p.get().setOnReadyListener(new OnReadyListener<Product>() {
            @Override
            public void onReady(Product product) {
                Log.d(TAG, product.toString());
                Toast.makeText(MainActivity.this, product.name, Toast.LENGTH_SHORT).show();
                testTextView.setText(product.toString());
                if(!product.picturesLinks.isEmpty()){
                    PictureLoader pl = new PictureLoader();
                    pl.getPicture(MainActivity.this, (ImageView)findViewById(R.id.profilepic_imageView), product.picturesLinks.get(0));
                }
            }
        });

    }

    public void newProduct(View view){
        Product p = new Product(this, "Sac Adadas", "", 10);
        p.publish();
    }

    public void newProductWithPic(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PRODUCT_PICTURE);
    }

    public void setProfilePic(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PROFILE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_PROFILE_PICTURE) {
            Log.d(TAG, data.getData().toString());
            PictureLoader pl = new PictureLoader();
            pl.setProfilePicture(this, data.getData());

        }else if(resultCode == RESULT_OK && requestCode == PICK_PRODUCT_PICTURE) {
            Log.d(TAG, data.getData().toString());
            Product p = new Product(this, "Sac Adadas", "", 10);
            p.picturesArray.add(data.getData());
            p.publish();
        }
    }

    public void getProfilePic(View view){
        ImageView imageView = findViewById(R.id.profilepic_imageView);
        PictureLoader pl = new PictureLoader();
        pl.getProfilePicture(this, imageView, mAuth.getCurrentUser().getUid());
    }

    public void getChat(View view){
        Chat c = new Chat("ngaTs7i0u0gvD4FvJmNci7gKspK2").setOnReadyListener(new OnReadyListener<Chat>() {
            @Override
            public void onReady(Chat c) {
                Log.d(TAG, c.messages.toString());
                testTextView.setText(c.messages.toString());
            }
        });
    }

    public void getAllMyContacts(View view){
        new Chat().allMyContacts().setOnReadyListener(new OnReadyListener<ArrayList<String>>() {
            @Override
            public void onReady(ArrayList<String> contacts) {
                testTextView.setText(contacts.toString());
            }
        });
    }

    public void addMessage(View view){
        Chat c = new Chat("ngaTs7i0u0gvD4FvJmNci7gKspK2").setOnReadyListener(new OnReadyListener<Chat>() {
            @Override
            public void onReady(Chat c) {
                c.setOnReadyListener(null).addMessage("Coucou !");
            }
        });
    }
}
