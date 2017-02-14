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
import android.widget.AdapterView;
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
import com.groupe6al2.bubbletalk.Class.ConnectedList;
import com.groupe6al2.bubbletalk.Class.User;
import com.groupe6al2.bubbletalk.R;

import java.util.ArrayList;
import java.util.Map;


public class ListConnectedFragment extends Fragment {

    View testRootView;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference myRef;
    User currentUser;
    ArrayList<User> userArrayList;

    String mUsername;
    String idBubble;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //System.out.println("On create");
        userArrayList= new ArrayList<>();
        final View rootView= inflater.inflate(R.layout.fragment_list_connected, container, false);
        testRootView = rootView;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

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





        final String[] uidStr = {""};
        final DatabaseReference databaseReference = database.getReference("chat").child(idBubble).child("userConnected");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot uid : dataSnapshot.getChildren()) {
                    uidStr[0] = uid.getKey();
                    DatabaseReference databaseReferenceUser = database.getReference("User").child(uidStr[0]);
                    databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            userArrayList.add(dataSnapshot2.getValue(User.class));
                            userArrayList.get(userArrayList.size()-1).setId(dataSnapshot2.getKey());
                            userArrayList.get(userArrayList.size()-1).setName((String) dataSnapshot2.child("nom").getValue());
                            refreshList();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //System.out.println("onViewCreated");

    }


    @Override
    public void onStart() {
        super.onStart();

       // System.out.println("start");
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void refreshList(){
        ListView listView = null;
        listView = (ListView) testRootView.findViewById(R.id.listViewConnected);
        String[] nameUser = new String[userArrayList.size()];
        String[] idUser = new String[userArrayList.size()];
        for(int i = 0; i<userArrayList.size(); i++){
            if(userArrayList.get(i).getUsePseudo()==true) {
                nameUser[i] = userArrayList.get(i).getPseudo();
            }else{
                nameUser[i] = userArrayList.get(i).getName();
            }
            idUser[i] = userArrayList.get(i).getId();
        }
        ConnectedList adapter = new
                ConnectedList(getActivity(), nameUser, idUser);
        listView.setAdapter(adapter);
    }


}