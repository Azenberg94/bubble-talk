package com.groupe6al2.bubbletalk.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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

    BubbleTalkSQLite bubbleTalkSQLite;
    ListView listViewMyBubble;
    ListView listViewProche;
    CustomList adapter2 = null;
    CustomList adapter1= null;
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



        bubbleTalkSQLite = new BubbleTalkSQLite(this);

        listViewMyBubble = (ListView) findViewById(R.id.listViewMyBubble);
        listViewProche = (ListView) findViewById(R.id.listViewProche);

        //Shared pref
        shre = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("bubble");
        b = new ArrayList<>();


        double latitude = 0;
        double longitude = 0;
        Location location = null;
        LocationManager mlocManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (mlocManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("test");
                return;
            }
            location = mlocManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        Query query =myRef.orderByChild("name");
        final double finalLongitude = longitude;
        final double finalLatitude = latitude;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Bubble.class).getEtat().equals("true")) {

                    double testDistance = Utils.Distance(Double.parseDouble(dataSnapshot.getValue(Bubble.class).getLatitude()), Double.parseDouble(dataSnapshot.getValue(Bubble.class).getLongitude()), finalLatitude, finalLongitude);
                   // System.out.println("myDist = " + testDistance);
                    if(testDistance<50) {
                        b.add(dataSnapshot.getValue(Bubble.class));
                        b.get(b.size() - 1).setId(dataSnapshot.getKey());
                        refreshBubbleProche();
                    }
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
        adapter1 = new
                CustomList(BubbleActivity.this, nameBubble, image, idMyBubble);
                listViewMyBubble.setAdapter(adapter1);

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
        String[] nameBubble = null;
        String[] image = null;
        final String[] idBubbleProche;

        if(Utils.isConnectedInternet(this)==false) {
            nameBubble = new String[1];
            nameBubble[0]= "Veuillez verifiez votre connexion internet.";
            image = new String[1];
            image[0]="";
            idBubbleProche = new String[1];
            idBubbleProche[0] ="";

            CustomList adapter2 = new
                    CustomList(BubbleActivity.this, nameBubble, image, idBubbleProche);
            listViewProche.setAdapter(adapter2);

        }else{

            nameBubble = new String[b.size()];
            image = new String[b.size()];
            idBubbleProche = new String[b.size()];

            for(int i = 0; i<b.size(); i++){
                idBubbleProche[i] = b.get(i).getId();
                nameBubble[i] = b.get(i).getName();
                image[i]="myGlideFirebase";
            }
            adapter2 = new
                    CustomList(BubbleActivity.this, nameBubble, image, idBubbleProche);
            listViewProche.setAdapter(adapter2);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        listViewMyBubble.setAdapter(null);
        listViewProche.setAdapter(null);
    }
}
