package com.groupe6al2.bubbletalk.Activity;

import android.Manifest;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.groupe6al2.bubbletalk.Class.Bubble;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.Utils;
import com.groupe6al2.bubbletalk.R;
import com.groupe6al2.bubbletalk.Widget.BubbleOnOff;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseError;


import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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
        userid = getIntent().getStringExtra("USER_ID");

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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 17.0f));
        googleMap.setOnMarkerClickListener(this);





        final double finalLongitude = longitude;
        final double finalLatitude = latitude;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Bubble.class).getEtat().equals("true")) {
                    double testDistance = Utils.Distance(Double.parseDouble(dataSnapshot.getValue(Bubble.class).getLatitude()), Double.parseDouble(dataSnapshot.getValue(Bubble.class).getLongitude()), finalLatitude, finalLongitude);
                    if(testDistance<50) {
                        Bubble b = dataSnapshot.getValue(Bubble.class);
                        b.setId(dataSnapshot.getKey());
                        addBubbles(b);
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

    public void addBubbles(Bubble bubble){
        int strokeColor;
        LatLng bubbleLocation;
        Log.i("onCall", "----------------------------------------------------------------------------1");
        bubbleLocation = new LatLng(Double.parseDouble(bubble.getLatitude()), Double.parseDouble(bubble.getLongitude()));
        if(bubble.getProprio().equals(userid)){
            strokeColor = Color.BLUE;
        }else strokeColor = Color.GRAY;
        Log.i("USERID", userid);
        Log.i("USERID",bubble.getProprio());
        mMap.addMarker(new MarkerOptions()
                .position(bubbleLocation)
                .snippet(bubble.getId())
                .title(bubble.getName())
                .alpha(0)
                );
        mMap.addCircle(new CircleOptions()
                .center(bubbleLocation)
                .radius(20)
                .strokeColor(strokeColor)
                .fillColor(Color.TRANSPARENT)
        );
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(MapsActivity.this, ChatActivity.class);
        intent.putExtra("id",   marker.getSnippet());
        startActivity(intent);
        return false;
    }
}
