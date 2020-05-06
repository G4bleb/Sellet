package com.uqac.sellet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.uqac.sellet.entities.PictureLoader;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            getProfilePic(view);
            getEmail(view);
        }

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
