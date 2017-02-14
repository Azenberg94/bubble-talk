package com.groupe6al2.bubbletalk.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.Chat;
import com.groupe6al2.bubbletalk.Class.ChatListAdapter;
import com.groupe6al2.bubbletalk.Class.User;
import com.groupe6al2.bubbletalk.R;


public class ListConnectedFragment extends Fragment {


    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference myRef;
    User currentUser;

    String mUsername;
    String idBubble;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;
    BubbleTalkSQLite bubbleTalkSQLite= new BubbleTalkSQLite(getActivity());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView= inflater.inflate(R.layout.fragment_chat, container, false);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        String myUid = user.getUid();
        myRef = database.getReference("User").child(myUid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(String.valueOf(snapshot.child("usePseudo").getValue()).equals("true")){
                    mUsername = (String) snapshot.child("pseudo").getValue();
                }else{
                    mUsername = (String) snapshot.child("nom").getValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Intent myIntent = getActivity().getIntent();
        idBubble = myIntent.getStringExtra("id");

        return rootView;

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void onStart() {
        super.onStart();

    }

}