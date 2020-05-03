package com.uqac.sellet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private static final int PICK_PROFILE_PICTURE = 1;
    private static final int PICK_PRODUCT_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        if(currentUser == null){
            Intent intent = new Intent(this, EmailPasswordActivity.class);
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
                ((TextView) findViewById(R.id.testtextview)).setText(product.toString());
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
}
