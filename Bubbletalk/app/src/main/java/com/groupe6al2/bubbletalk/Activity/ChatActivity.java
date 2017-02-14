package com.groupe6al2.bubbletalk.Activity;


import android.content.Intent;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
    String fActuel ="";
    ChatFragment chatFragment = new ChatFragment();
    ListConnectedFragment listConnectedFragment = new ListConnectedFragment();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;
    BubbleTalkSQLite bubbleTalkSQLite= new BubbleTalkSQLite(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

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


        fActuel = "ChatFragment";
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.add(R.id.fragment_container, chatFragment);


        fragmentManager.commit();
        ImageButton buttonU = (ImageButton) findViewById(R.id.buttonU);
        buttonU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment();
            }
        });

        addConnectedToChat(idBubble, user.getUid());

    }

    private void addConnectedToChat(String idBubble, String uid) {
        DatabaseReference databaseReference = database.getReference("chat").child(idBubble).child("userConnected").child(uid);
        databaseReference.setValue("1");
    }

    private void deleteConnectedToChat(String idBubble, String uid) {
        DatabaseReference databaseReference = database.getReference("chat").child(idBubble).child("userConnected").child(uid);
        databaseReference.removeValue();
    }

    private void switchFragment() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        if(fActuel.equals("ChatFragment")){
            fActuel="ListConnectedFragment";
            fragmentManager.replace(R.id.fragment_container, listConnectedFragment);
        }else{
            fActuel = "ChatFragment";
            fragmentManager.replace(R.id.fragment_container, chatFragment);
        }
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

    @Override
    protected void onDestroy() {
        deleteConnectedToChat(idBubble, user.getUid());
        super.onDestroy();

    }
}
