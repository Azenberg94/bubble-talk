package com.groupe6al2.bubbletalk.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groupe6al2.bubbletalk.Class.Bubble;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.Utils;
import com.groupe6al2.bubbletalk.R;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static com.groupe6al2.bubbletalk.Class.Utils.returnHex;

public class CreateBubbleActivity extends AppCompatActivity {

    boolean initialAvatar = true;
    EditText editTextNameCreateBubble;
    EditText editTextDescriptionCreateBubble;
    ImageView imageView;
    LocationManager manager;
    NetworkInfo network;
    byte[] avatarBubbleDisplay  = new byte[0];;
    byte[] avatarBubblebefore  = new byte[0];;
    Utils utils = new Utils();
    double longitude;
    double latitude;

    FirebaseUser user;
    FirebaseAuth auth;

    SharedPreferences shre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bubble);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        //Shared pref
        shre = PreferenceManager.getDefaultSharedPreferences(this);

        network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        Button buttonValidateCreateBubble = (Button) findViewById(R.id.buttonValidateCreateBubble);
        buttonValidateCreateBubble.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createBubble();
            }
        });

        editTextNameCreateBubble = (EditText) findViewById(R.id.editTextNameCreateBubble);
        editTextDescriptionCreateBubble = (EditText) findViewById(R.id.editTextDescriptionCreateBubble);
        imageView = (ImageView) findViewById(R.id.imageViewCreateBubble);

        Button buttonUpdateAvatarBubble = (Button) findViewById(R.id.buttonUpdateAvatarBubble);
        buttonUpdateAvatarBubble.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });
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

            avatarBubbleDisplay = utils.readBytesFromFile(picturePath);

            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();

            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBubbleDisplay,0,avatarBubbleDisplay.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,stream);
            avatarBubbleDisplay = stream.toByteArray();

            imageView.setImageBitmap(bitmap);
        }
    }

    public void createBubble(){
        if(editTextNameCreateBubble.getText().toString().equals("")){
            Toast.makeText(CreateBubbleActivity.this, "Veuillez saisir un nom !",Toast.LENGTH_SHORT).show();
        }else if(editTextNameCreateBubble.getText().toString().length()<3 || editTextNameCreateBubble.getText().toString().length()>50 ){
            Toast.makeText(CreateBubbleActivity.this, "Le nom de bulle doit faire entre 3 et 50 caractères pour pouvoir être utilisé !",Toast.LENGTH_SHORT).show();
        }else if (editTextDescriptionCreateBubble.getText().toString().length()>50) {
            Toast.makeText(CreateBubbleActivity.this, "La description ne peut dépasser les 50 caractères !",Toast.LENGTH_SHORT).show();
        }else if(network==null || !network.isConnected()) {
            Toast.makeText(CreateBubbleActivity.this, "Veuillez verifiez votre connexion internet !",Toast.LENGTH_SHORT).show();
        }else{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("bubble").push();
            databaseReference.child("name").setValue(editTextNameCreateBubble.getText().toString());
            databaseReference.child("description").setValue(editTextDescriptionCreateBubble.getText().toString());
            databaseReference.child("proprio").setValue(user.getUid());

            String myId = databaseReference.getKey();
            updateFirebaseStorage(myId);
            String myMd5 ="";
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
                md.update(avatarBubbleDisplay);
                byte[] hash = md.digest();
                myMd5 = returnHex(hash);
                databaseReference.child("md5Bubble").setValue(myMd5);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bubble bubble = new Bubble( myId, editTextNameCreateBubble.getText().toString(), editTextDescriptionCreateBubble.getText().toString(), user.getUid(), myMd5);
            BubbleTalkSQLite bubbleTalkSQLite = new BubbleTalkSQLite(this);
            bubbleTalkSQLite.addBubble(bubble);

            Toast.makeText(CreateBubbleActivity.this, "Votre Bubble a été créee avec succès !",Toast.LENGTH_SHORT).show();

            finish();
        }
    }



    public void updateFirebaseStorage(String id){

        String encodedImage = Base64.encodeToString(avatarBubbleDisplay, Base64.DEFAULT);
        SharedPreferences.Editor edit=shre.edit();
        edit.putString("bubble_"+id,encodedImage);
        edit.commit();


        // Points to the root reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");
        StorageReference avatarFileRef = storageRef.child("bubble/"+id);

        UploadTask uploadTask = avatarFileRef.putBytes(avatarBubbleDisplay);
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

}
