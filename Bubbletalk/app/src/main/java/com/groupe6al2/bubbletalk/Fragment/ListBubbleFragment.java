package com.groupe6al2.bubbletalk.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groupe6al2.bubbletalk.Class.Bubble;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.R;

import java.util.ArrayList;

public class ListBubbleFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    BubbleTalkSQLite bubbleTalkSQLite;
    ListView listViewMy;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bubbleTalkSQLite = new BubbleTalkSQLite(context);
        System.out.println("test 2 : ");
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("bubble");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");
        //StorageReference avatarFileRef = storageRef.child("bubble/"+id);

        refreshMy(view);
    }

    private void refreshMy(View view) {

        //listViewMy = (ListView) view.findViewById(R.id.listViewBubbleMy);
        ArrayList<Bubble> bubbleArrayList = bubbleTalkSQLite.getMyBubbles();


        System.out.println("test 2 : ");
        for(int i=0 ; i<bubbleArrayList.size(); i++){
            System.out.println("test : " + i);
        }

    }

}
