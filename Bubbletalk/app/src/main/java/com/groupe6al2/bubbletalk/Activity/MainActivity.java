package com.groupe6al2.bubbletalk.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.groupe6al2.bubbletalk.R;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

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
        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        email =  user.getEmail();
        name = user.getDisplayName();
        uid = user.getUid();
        bubbleTalkSQLite= new BubbleTalkSQLite(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User").child(user.getUid());


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("email").getValue() != null && dataSnapshot.child("nom").getValue() != null ) {
                    String value = dataSnapshot.getValue().toString();
                    if(!dataSnapshot.child("email").getValue().equals(email)) {
                        bubbleTalkSQLite.updateUser(user.getUid(), new String[]{"",email,"","",""} );
                        myRef.child("email").setValue(email);
                    }
                    if(!dataSnapshot.child("nom").getValue().equals(name)){
                        myRef.child("nom").setValue(name);
                        bubbleTalkSQLite.updateUser(user.getUid(), new String[]{"","",name,"",""} );
                    }

                    //System.out.println("Mon user firebase : " + value);
                }else{
                   // System.out.println("New user");

                    myRef.child("email").setValue(email);
                    myRef.child("nom").setValue(name);
                    myRef.child("pseudo").setValue("");
                    myRef.child("usePseudo").setValue(false);
                    myRef.child("avatar").setValue("");

                    bubbleTalkSQLite.addUser(new User(uid, "", false, email, name, ""));

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("test", "Failed to read value.", error.toException());
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonParam = (Button) findViewById(R.id.buttonParam);
        buttonParam.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ParamActivity.class);
                startActivity(intent);
            }
        });

        buttonDeco = (Button)findViewById(R.id.buttonDeco);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



// Go button click event
        buttonDeco.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}



