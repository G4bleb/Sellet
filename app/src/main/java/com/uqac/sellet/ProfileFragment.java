package com.uqac.sellet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        getProfilePic(view);
        getEmail(view);

        return view;
    }

    private void getProfilePic(View view){
        ImageView imageView = view.findViewById(R.id.profile_image);
        PictureLoader pl = new PictureLoader();
        pl.getProfilePicture(getContext(), imageView, mAuth.getCurrentUser().getUid());
    }

    private void getEmail(View view) {
        TextView profileMail = view.findViewById(R.id.profile);
        profileMail.setText(mAuth.getCurrentUser().getEmail());
    }
}
