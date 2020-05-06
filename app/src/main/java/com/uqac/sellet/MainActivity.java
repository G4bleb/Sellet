package com.uqac.sellet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * MEMBRES DU GROUPE :
 * Pierre-Alban LAGADEC
 * Gabriel LEBIS
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private boolean setupDone = false;

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
            if(!setupDone){
                startTabs();
                setupDone = true;
            }
        }

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
