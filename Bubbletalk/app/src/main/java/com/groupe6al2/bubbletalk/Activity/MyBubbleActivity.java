package com.groupe6al2.bubbletalk.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groupe6al2.bubbletalk.Class.Bubble;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.R;
import com.groupe6al2.bubbletalk.Widget.BubbleOnOff;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.groupe6al2.bubbletalk.Class.Utils.readBytesFromFile;
import static com.groupe6al2.bubbletalk.Class.Utils.returnHex;

public class MyBubbleActivity extends AppCompatActivity {

    String idBubble;
    EditText editTextMyBubbleName;
    EditText editTextMyBubbleDescription;
    BubbleTalkSQLite bubbleTalkSQLite;
    byte[] avatarBefore = new byte[0];
    byte[] avatarDisplay = new byte[0];
    ImageView imageView;
    SharedPreferences shre;
    Bubble bubble;
    Button buttonActivate;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String action = "";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bubble);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        bubbleTalkSQLite = new BubbleTalkSQLite(this);

        //Shared pref
        shre = PreferenceManager.getDefaultSharedPreferences(this);

        Intent myIntent = getIntent();
        idBubble = myIntent.getStringExtra("id");
        bubble = bubbleTalkSQLite.getOneBubble(idBubble);

        editTextMyBubbleName = (EditText) findViewById(R.id.editTextMyBubbleName);
        editTextMyBubbleName.setText(bubble.getName());

        editTextMyBubbleDescription = (EditText) findViewById(R.id.editTextMyBubbleDescription);
        editTextMyBubbleDescription.setText(bubble.getDescription());

        imageView = (ImageView) findViewById(R.id.imageViewMyBubble);
        if (savedInstanceState != null) {
            if (savedInstanceState.getByteArray("myAvatar").length > 0) {
                avatarDisplay = savedInstanceState.getByteArray("myAvatar");
            }
        }
        if (avatarDisplay.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarDisplay, 0, avatarDisplay.length);
            imageView.setImageBitmap(null);
            imageView.setImageBitmap(bitmap);
        } else if (!shre.getString("bubble_" + idBubble, "").equals("")) {
            avatarBefore = Base64.decode(shre.getString("bubble_" + idBubble, ""), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBefore, 0, avatarBefore.length);
            imageView.setImageBitmap(null);
            imageView.setImageBitmap(bitmap);
        }


        Button buttonUpdateAvatar = (Button) findViewById(R.id.buttonUpdateAvatarMyBubble);
        buttonUpdateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });

        Button buttonSave = (Button) findViewById(R.id.buttonSaveMyBubble);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBubble();
            }
        });

        Button buttonDelete = (Button) findViewById(R.id.buttonDeleteMyBubble);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Confirmation")
                        .setMessage("Supprimer la Bubble ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteBubble();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        buttonActivate = (Button) findViewById(R.id.buttonMyBubbleActivate);
        buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonActivate.getText().equals("Stopper la bubble"))
                {
                    buttonActivate.setText("Démarrer la bubble");
                } else {
                    buttonActivate.setText("Stopper la bubble");
                }
                activateBubble();
            }
        });


        Button buttonMyBubbleGoToChat = (Button) findViewById(R.id.buttonMyBubbleGoToChat);
        buttonMyBubbleGoToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBubble();
            }
        });


        final DatabaseReference myRef = database.getReference("bubble").child(idBubble);
        //to initialize my button
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("etat").getValue().equals("true")) {
                    buttonActivate.setText("Stopper la bubble");
                } else {
                    buttonActivate.setText("Démarrer la bubble");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void activateBubble() {
        double latitude = 0;
        double longitude = 0;
        Location location = null;
        LocationManager mlocManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (mlocManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = mlocManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("bubble").child(idBubble);
        
        final double finalLongitude = longitude;
        final double finalLatitude = latitude;
        final boolean[] activate = {true};


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Intent intent = new Intent(BubbleOnOff.STATE_CHANGE);
                if(snapshot.child("etat").getValue().equals("true")){
                    intent.putExtra("State", true);
                    myRef.child("etat").setValue("false");
                    activate[0] = false;
                }else{
                    intent.putExtra("State", true);
                    myRef.child("etat").setValue("true");
                    myRef.child("longitude").setValue(String.valueOf(finalLongitude));
                    myRef.child("latitude").setValue(String.valueOf(finalLatitude));
                }
                getApplicationContext().sendBroadcast(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void goToBubble() {
        Intent intent = new Intent(MyBubbleActivity.this, ChatActivity.class);
        intent.putExtra("id", idBubble);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if(readBytesFromFile(picturePath).length/(1024*1024)>4){
                Toast.makeText(this, "Image trop lourde ! 4mo max",Toast.LENGTH_SHORT).show();
            }else {
                avatarDisplay = readBytesFromFile(picturePath);
                // Get the data from an ImageView as bytes
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                imageView.setImageBitmap(null);
                // byte[] test = Base64.decode(myBtoS, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(avatarDisplay, 0, avatarDisplay.length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                avatarDisplay = stream.toByteArray();

                imageView.setImageBitmap(bitmap);
            }

        }
    }


    public void updateBubble(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        boolean testUpdate = false;
        String name="";
        String description ="";
        String avatar="";

        if(!bubble.getName().equals(editTextMyBubbleName.getText().toString())){
            if(editTextMyBubbleName.length()>2) {
                name = editTextMyBubbleName.getText().toString();
                bubble.setName(name);
                database.getReference("bubble").child(idBubble).child("name").setValue(name);
                testUpdate = true;
            }else{
                Toast.makeText(this, "Votre nom de Bubble doit faire au moins 3 caractères !",Toast.LENGTH_SHORT).show();
            }
        }

        if(!bubble.getDescription().equals(editTextMyBubbleDescription.getText().toString())){
            if(editTextMyBubbleDescription.length()<50) {
                description = editTextMyBubbleDescription.getText().toString();
                bubble.setDescription(description);
                database.getReference("bubble").child(idBubble).child("description").setValue(description);
                testUpdate = true;
            }else{
                Toast.makeText(this, "Votre description de Bubble ne doit faire plus de 50 caractères !",Toast.LENGTH_SHORT).show();
            }
        }

        if(avatarBefore!=avatarDisplay & avatarDisplay.length>0){

            avatarBefore = avatarDisplay;
            updateFirebaseAndPreferenceStorage();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(avatarDisplay);
                byte[] hash = md.digest();
                avatar = returnHex(hash);
                database.getReference("bubble").child(idBubble).child("md5Bubble").setValue(avatar);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            testUpdate = true;
        }

        if(testUpdate == true) {
            bubbleTalkSQLite.updateBubble(idBubble, new String[]{name, description,avatar});
            Toast.makeText(this, "Modification sauvegardée !", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateFirebaseAndPreferenceStorage(){

        String encodedImage = Base64.encodeToString(avatarDisplay, Base64.DEFAULT);
        SharedPreferences.Editor edit=shre.edit();
        edit.putString("bubble_"+idBubble,encodedImage);
        edit.commit();


        // Points to the root reference

        StorageReference storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");
        StorageReference avatarFileRef = storageRef.child("bubble/"+idBubble+"");


        UploadTask uploadTask = avatarFileRef.putBytes(avatarDisplay);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public void deleteBubble() {

        //Update to etat false
        database.getReference("bubble").child(idBubble).child("etat").setValue("false");

        bubbleTalkSQLite.deleteMyBubble(idBubble);
        Toast.makeText(this, "La Bubble a été éclatée !",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, BubbleActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {

        imageView.setImageBitmap(null);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if(avatarDisplay.length>0){
            imageView.setImageBitmap(null);
        }
        outState.putByteArray("myAvatar", avatarDisplay);

    }


}
