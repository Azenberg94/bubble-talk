package com.groupe6al2.bubbletalk.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.User;
import com.groupe6al2.bubbletalk.Class.Utils;
import com.groupe6al2.bubbletalk.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String USER_ID = "USER_ID";
    FirebaseUser user;
    Button buttonDeco;
    FirebaseAuth auth;
    DatabaseReference myRef;
    String email;
    String name;
    String uid;
    BubbleTalkSQLite bubbleTalkSQLite;

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        email =  user.getEmail();
        name = user.getDisplayName();
        uid = user.getUid();
        bubbleTalkSQLite= new BubbleTalkSQLite(this);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User").child(user.getUid());

        //Create new user if not exist + check if mail and name not changed
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("email").getValue() != null && dataSnapshot.child("nom").getValue() != null ) {
                    String value = dataSnapshot.getValue().toString();
                    if(!dataSnapshot.child("email").getValue().equals(email)) {
                        bubbleTalkSQLite.updateUser(user.getUid(), new String[]{"",email,"","",""});
                        myRef.child("email").setValue(email);
                    }
                    if(!dataSnapshot.child("nom").getValue().equals(name)){
                        myRef.child("nom").setValue(name);
                        bubbleTalkSQLite.updateUser(user.getUid(), new String[]{"","",name,"",""});
                    }

                    //System.out.println("Mon user firebase : " + value);
                }else{
                    // System.out.println("New user");

                    myRef.child("email").setValue(email);
                    myRef.child("nom").setValue(name);
                    myRef.child("pseudo").setValue("");
                    myRef.child("usePseudo").setValue(false);

                    bubbleTalkSQLite.addUser(new User(uid, "", false, email, name, ""));

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("test", "Failed to read value.", error.toException());
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_bubble) {
            Intent intent = new Intent(MainActivity.this, BubbleActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create_bubble) {
            Intent intent = new Intent(MainActivity.this, CreateBubbleActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_see_map){
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra(USER_ID, uid);
            startActivity(intent);
        } else if (id == R.id.nav_parametre) {
            if(Utils.isConnectedInternet(this)==true) {
                Intent intent = new Intent(MainActivity.this, ParamActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(MainActivity.this, "Vous devez être connecté à internet pour modifier votre profil !",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_deco) {
            auth.signOut();
            // Google sign out
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }





}
