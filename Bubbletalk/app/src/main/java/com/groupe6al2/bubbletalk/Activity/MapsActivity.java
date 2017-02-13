package com.groupe6al2.bubbletalk.Activity;

import android.Manifest;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.groupe6al2.bubbletalk.Class.Bubble;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.R;
import com.groupe6al2.bubbletalk.Widget.BubbleOnOff;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseError;


import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    MapFragment mMapFragment;
    FragmentTransaction fragmentTransaction;
    BubbleTalkSQLite bubbleTalkSQLite;
    String userid;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference("bubble");

    Query query = myRef.orderByChild("name");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        userid = getIntent().getStringExtra("id");

        bubbleTalkSQLite = new BubbleTalkSQLite(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       /* mMapFragment = MapFragment.newInstance();
        fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
*/

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {

            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        Location location = new Location("");
        LatLng currentLocation = new LatLng(48.6693, 2.3598);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12.0f));

        Intent intent = new Intent(BubbleOnOff.STATE_CHANGE);
        intent.putExtra("State", true);
        Log.i("onCall", "----------------------------------------------------------------------------1");
        getApplicationContext().sendBroadcast(intent);



        ArrayList<Bubble> bubbles = bubbleTalkSQLite.getAllActiveBubbles();
        addBubbles(bubbles);




    }


    query.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getValue(Bubble.class).getEtat().equals("true")) {
                Bubble b = dataSnapshot.getValue(Bubble.class);
                b.setId(dataSnapshot.getKey());
                addBubbles(b);
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

    public void addBubbles(ArrayList<Bubble> bubbles){
        int strokeColor;
        LatLng bubbleLocation;
        Log.i("onCall", "----------------------------------------------------------------------------1");
        for (Bubble bubble : bubbles)
        {
            bubbleLocation = new LatLng(Double.parseDouble(bubble.getLatitude()), Double.parseDouble(bubble.getLongitude()));
            if(bubble.getProprio()==userid){
                strokeColor = Color.BLUE;
            }else strokeColor = Color.GRAY;
            mMap.addCircle(new CircleOptions()
                    .center(bubbleLocation)
                    .radius(50)
                    .strokeColor(strokeColor)
                    .fillColor(Color.TRANSPARENT)
                    );
        }
    }


}
