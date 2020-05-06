package com.uqac.sellet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private static final int PICK_PROFILE_PICTURE = 1;
    private static final int PICK_PRODUCT_PICTURE = 2;

    int[] tabIcons = {
            R.drawable.logo,
            R.drawable.ic_search_white_24dp,
            R.drawable.ic_add_circle_outline_white_24dp,
            R.drawable.ic_chat_bubble_outline_white_24dp,
            R.drawable.ic_person_outline_white_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivityForResult(intent, RESULT_OK);
        } else {
            startTabs();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        startTabs();
    }

    public void startTabs() {
        ViewPager mViewPager = findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(4);

        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        for(int i=0; i<tabLayout.getTabCount(); i++){
            if(tabLayout.getTabAt(i) != null){
                tabLayout.getTabAt(i).setIcon(tabIcons[i]);
                if(i>0) tabLayout.getTabAt(i).getIcon().setColorFilter(ContextCompat.getColor(this, R.color.primaryText), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    /*public void getProduct(View view){
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
        Intent myIntent = new Intent(MainActivity.this, ChattingActivity.class);
        myIntent.putExtra("himId", "ngaTs7i0u0gvD4FvJmNci7gKspK2"); //Optional parameters
        MainActivity.this.startActivity(myIntent);

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

    */

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(this, getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "");
        adapter.addFragment(new SearchFragment(), "");
        adapter.addFragment(new AddFragment(), "");
        adapter.addFragment(new ChatsFragment(), "");
        adapter.addFragment(new ProfileFragment(), "");

        viewPager.setAdapter(adapter);
    }
}
