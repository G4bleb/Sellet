package com.uqac.sellet;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.uqac.sellet.entities.Chat;
import com.uqac.sellet.entities.OnReadyListener;
import com.uqac.sellet.entities.User;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";
    private FirebaseAuth mAuth;

    private LinearLayout listOfChats;
    private ArrayList<User> myContacts = new ArrayList<>();
    private PictureLoader pl = new PictureLoader();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chats_fragment, container, false);

        listOfChats = view.findViewById(R.id.chatsList);
        new Chat().allMyContacts().setOnReadyListener(new OnReadyListener<ArrayList<String>>() {
            @Override
            public void onReady(ArrayList<String> contactsIds) {
                loadUsersfromContacts(contactsIds);
            }
        });

        return view;
    }

    private void loadUsersfromContacts(ArrayList<String> contacts){
        for(String contact : contacts){
            new User(getContext(), contact).setOnReadyListener(new OnReadyListener<User>() {
                @Override
                public void onReady(User u) {
                    myContacts.add(u);
                    addHimToChatsList(u);
                }
            });
        }
    }

    private void addHimToChatsList(User him){
        LinearLayout chatView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.chats_row, listOfChats, false);

        ((TextView)(chatView.findViewById(R.id.hisName))).setText(him.name);
        pl.getProfilePicture(getContext(), chatView.findViewById(R.id.hisProfilePicture), him.uid);

        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), ChattingActivity.class);
                myIntent.putExtra("himId", him.uid); //Optional parameters
                ChatsFragment.this.startActivity(myIntent);
            }
        });

        listOfChats.addView(chatView);
    }
}
