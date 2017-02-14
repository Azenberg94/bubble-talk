package com.groupe6al2.bubbletalk.Activity;


import android.content.Intent;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.ChatListAdapter;
import com.groupe6al2.bubbletalk.Class.User;
import com.groupe6al2.bubbletalk.Fragment.ChatFragment;
import com.groupe6al2.bubbletalk.Fragment.ListConnectedFragment;
import com.groupe6al2.bubbletalk.R;

public class ChatActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference myRef;
    User currentUser;

    String mUsername;
    String idBubble;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;
    BubbleTalkSQLite bubbleTalkSQLite= new BubbleTalkSQLite(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent myIntent = getIntent();
        idBubble = myIntent.getStringExtra("id");

        DatabaseReference databaseReference = database.getReference("bubble").child(idBubble);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                setTitle(String.valueOf(snapshot.child("name").getValue()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ChatFragment chatFragment = new ChatFragment();
        ListConnectedFragment listConnectedFragment = new ListConnectedFragment();

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.add(R.id.fragment_container, chatFragment);
        fragmentManager.add(R.id.fragment_container, listConnectedFragment);

        fragmentManager.commit();



    }


   /* public void updateOtherFragments(Fragment currentFragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        for(Fragment fragment : fragmentManager.getFragments()){
            if(currentFragment.getId() != fragment.getId()){
                ((ChatFragment)fragment).updateTextViewFragments();
            }
        }
    }*/
}
