package com.groupe6al2.bubbletalk.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groupe6al2.bubbletalk.Class.Bubble;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.CustomList;
import com.groupe6al2.bubbletalk.Class.Utils;
import com.groupe6al2.bubbletalk.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BubbleActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    BubbleTalkSQLite bubbleTalkSQLite;
    ListView listViewMyBubble;
    ListView listViewProche;

    SharedPreferences shre;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapterMyBubble;
    ArrayAdapter<String> adapterProche;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItemsMyBubble=new ArrayList<String>();
    ArrayList<String> listItemsProche=new ArrayList<String>();

    //Tab for id ONCLIC
    String[] idMyBubble;

    ArrayList<Bubble> b;

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
        listViewProche = (ListView) findViewById(R.id.listViewProche);

        //Shared pref
        shre = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("bubble");
        b = new ArrayList<>();

        Query query =myRef.orderByChild("name");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Bubble.class).getEtat().equals("true")) {
                    b.add(dataSnapshot.getValue(Bubble.class));
                    b.get(b.size() - 1).setId(dataSnapshot.getKey());
                    refreshBubbleProche();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshMyBubble();

    }

    private void refreshMyBubble() {
        ArrayList<Bubble> bubbleArrayList = bubbleTalkSQLite.getMyBubbles(user.getUid());

        idMyBubble = new String[bubbleArrayList.size()];
        String[] nameBubble = new String[bubbleArrayList.size()];
        String[] image = new String[bubbleArrayList.size()];

        for(int i=0 ; i<bubbleArrayList.size(); i++){
            nameBubble[i] = bubbleArrayList.get(i).getName();
            idMyBubble[i] = bubbleArrayList.get(i).getId();
            image[i] = shre.getString("bubble_" + bubbleArrayList.get(i).getId(), "");
        }
        CustomList adapter = new
                CustomList(BubbleActivity.this, nameBubble, image);
                listViewMyBubble.setAdapter(adapter);

        listViewMyBubble.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BubbleActivity.this, MyBubbleActivity.class);
                intent.putExtra("id", idMyBubble[position]);
                startActivity(intent);
            }
        });


    }

    private void refreshBubbleProche() {
        String[] nameBubble;
        String[] image;
        final String[] idBubbleProche;

        if(Utils.isConnectedInternet(this)==false) {
            nameBubble = new String[1];
            nameBubble[0]= "Veuillez verifiez votre connexion internet.";
            image = new String[1];
            image[0]="";


            CustomList adapter = new
                    CustomList(BubbleActivity.this, nameBubble, image);
            listViewProche.setAdapter(adapter);

        }else{

            nameBubble = new String[b.size()];
            image = new String[b.size()];
            idBubbleProche = new String[b.size()];

            for(int i = 0; i<b.size(); i++){
                idBubbleProche[i] = b.get(i).getId();
                nameBubble[i] = b.get(i).getName();
                image[i]="";

            }
            CustomList adapter = new
                    CustomList(BubbleActivity.this, nameBubble, image);
            listViewProche.setAdapter(adapter);
            listViewProche.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(BubbleActivity.this, ChatActivity.class);
                    intent.putExtra("id", idBubbleProche[position]);
                    startActivity(intent);
                }
            });
        }

    }




}
