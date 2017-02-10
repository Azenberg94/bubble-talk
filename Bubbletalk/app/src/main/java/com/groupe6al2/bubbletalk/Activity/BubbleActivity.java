package com.groupe6al2.bubbletalk.Activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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

public class BubbleActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    BubbleTalkSQLite bubbleTalkSQLite;
    ListView listViewMyBubble;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("bubble");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");

        bubbleTalkSQLite = new BubbleTalkSQLite(this);

        listViewMyBubble = (ListView) findViewById(R.id.listViewMyBubble);


        refreshMy();
    }


    private void refreshMy() {


        ArrayList<Bubble> bubbleArrayList = bubbleTalkSQLite.getMyBubbles();
        for(int i=0 ; i<bubbleArrayList.size(); i++){
            listItems.add(bubbleArrayList.get(i).getName());
        }
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listViewMyBubble.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}
