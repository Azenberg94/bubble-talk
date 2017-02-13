package com.groupe6al2.bubbletalk.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groupe6al2.bubbletalk.Class.Bubble;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.R;

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

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bubble);

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


        avatarBefore = Base64.decode(shre.getString("bubble_" + bubble.getId(), ""), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBefore, 0, avatarBefore.length);
        imageView = (ImageView) findViewById(R.id.imageViewMyBubble);
        imageView.setImageBitmap(null);
        imageView.setImageBitmap(bitmap);

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
        buttonDelete.setOnClickListener(new View.OnClickListener(){
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

    }

    private void activateBubble() {

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

            avatarDisplay = readBytesFromFile(picturePath);
            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();

            // byte[] test = Base64.decode(myBtoS, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarDisplay,0,avatarDisplay.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,stream);
            avatarDisplay = stream.toByteArray();
            System.out.println("AFTER : " + avatarDisplay.length);
            imageView.setImageBitmap(bitmap);

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

        if(avatarBefore!=avatarDisplay){

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
        StorageReference avatarFileRef = storageRef.child("bubble/"+idBubble+".jpg");


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
        bubbleTalkSQLite.deleteMyBubble(idBubble);
        Toast.makeText(this, "La Bubble a été éclatée !",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, BubbleActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onStop();
    }
}
