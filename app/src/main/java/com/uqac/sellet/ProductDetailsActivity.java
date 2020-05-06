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

import com.google.firebase.firestore.FieldValue;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.uqac.sellet.entities.OnReadyListener;
import com.uqac.sellet.entities.PictureLoader;
import com.uqac.sellet.entities.Product;
import com.uqac.sellet.entities.User;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailsActivity";

    private Product product;
    private PictureLoader pl = new PictureLoader();

    private TextView name_textview;
    private CarouselView carouselView;
    private TextView desc_textview;
    private TextView price_textview;
    private ImageView seller_imageview;
    private TextView seller_name;

    private Button chatButton;
    private Button favButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        carouselView = findViewById(R.id.details_carousel);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                pl.getPicture(ProductDetailsActivity.this, imageView, product.picturesLinks.get(position));
            }
        });

        name_textview = findViewById(R.id.product_name);
        desc_textview = findViewById(R.id.product_desc);
        price_textview = findViewById(R.id.product_price);

        seller_imageview = findViewById(R.id.hisProfilePicture);
        seller_name = findViewById(R.id.hisName);

        chatButton = findViewById(R.id.contact_seller_btn);
        favButton = findViewById(R.id.fav_button);

        product = new Product(this, getIntent().getStringExtra("productId"));
        product.get().setOnReadyListener(new OnReadyListener<Product>() {
            @Override
            public void onReady(Product p) {
                product = p;
                name_textview.setText(product.name);
                desc_textview.setText(product.desc);
                price_textview.setText(Double.toString(product.price));

                setSeller(product.owner);

                if(!product.picturesLinks.isEmpty()){
                    refreshCarouselView();
                }
            }
        });

    }

    private void refreshCarouselView(){
        carouselView.setPageCount(product.picturesLinks.size());
    }


    private void setSeller(String userid){
        Log.d(TAG, "setSeller: "+userid);

        new User(this, userid).setOnReadyListener(new OnReadyListener<User>() {
            @Override
            public void onReady(User u) {
                seller_name.setText(u.name);
                pl.getProfilePicture(ProductDetailsActivity.this, seller_imageview, u.uid);
                chatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactSeller(u.uid);
                    }
                });
            }
        });
    }

    private void contactSeller(String uid){
        Intent myIntent = new Intent(this, ChattingActivity.class);
        myIntent.putExtra("himId", uid); //Optional parameters
        startActivity(myIntent);
    }

    public void favPressed(View v){
        User.addToFavorites(product.id);
        Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }
}
