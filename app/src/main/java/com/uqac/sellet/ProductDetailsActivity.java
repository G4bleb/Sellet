package com.uqac.sellet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.uqac.sellet.entities.OnReadyListener;
import com.uqac.sellet.entities.PictureLoader;
import com.uqac.sellet.entities.Product;
import com.uqac.sellet.entities.User;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailsActivity";

    private FirebaseAuth mAuth;

    private Product product;
    private PictureLoader pl = new PictureLoader();

    private User currentUser;

    private TextView name_textview;
    private CarouselView carouselView;
    private TextView desc_textview;
    private TextView price_textview;
    private ImageView seller_imageview;
    private TextView seller_name;

    private Button chatButton;
    private Button favButton;
    private boolean faved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mAuth = FirebaseAuth.getInstance();

        currentUser = new User(ProductDetailsActivity.this, mAuth.getCurrentUser().getUid()).setOnReadyListener((OnReadyListener<User>) u -> {
            if(currentUser.checkFaved(product.id)) {
                faved = true;
                swapFavIcon();
            }
        });

        carouselView = findViewById(R.id.details_carousel);
        carouselView.setImageListener((position, imageView) -> pl.getPicture(ProductDetailsActivity.this, imageView, product.picturesLinks.get(position)));

        name_textview = findViewById(R.id.product_name);
        desc_textview = findViewById(R.id.product_desc);
        price_textview = findViewById(R.id.product_price);

        seller_imageview = findViewById(R.id.hisProfilePicture);
        seller_name = findViewById(R.id.hisName);

        chatButton = findViewById(R.id.contact_seller_btn);
        favButton = findViewById(R.id.fav_button);

        product = new Product(this, getIntent().getStringExtra("productId"));
        product.get().setOnReadyListener((OnReadyListener<Product>) p -> {
            product = p;
            name_textview.setText(product.name);
            desc_textview.setText(product.desc);
            price_textview.setText(Double.toString(product.price) + "$");

            setSeller(product.owner);

            if(!product.picturesLinks.isEmpty()){
                refreshCarouselView();
            }
        });
    }

    private void refreshCarouselView(){
        carouselView.setPageCount(product.picturesLinks.size());
    }


    private void setSeller(String userid){
        Log.d(TAG, "setSeller: "+userid);

        new User(this, userid).setOnReadyListener((OnReadyListener<User>) u -> {
            seller_name.setText(u.name);
            pl.getProfilePicture(ProductDetailsActivity.this, seller_imageview, u.uid);
            chatButton.setOnClickListener(v -> contactSeller(u.uid));
        });
    }

    private void contactSeller(String uid){
        Intent myIntent = new Intent(this, ChattingActivity.class);
        myIntent.putExtra("himId", uid); //Optional parameters
        startActivity(myIntent);
    }

    public void favPressed(View v){
        if(!faved) {
            User.addToFavorites(product.id);
            Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
            faved = true;
            swapFavIcon();
        } else {
            User.removeFromFavorites(product.id);
            Toast.makeText(this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
            faved = false;
            swapFavIcon();
        }
    }

    public void swapFavIcon() {
        if (faved) {
            favButton.setBackground(getResources().getDrawable(R.drawable.faved_button));
        } else {
            favButton.setBackground(getResources().getDrawable(R.drawable.fav_button));
        }
    }
}
