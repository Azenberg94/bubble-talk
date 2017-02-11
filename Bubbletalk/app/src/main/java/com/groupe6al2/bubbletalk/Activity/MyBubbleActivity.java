package com.groupe6al2.bubbletalk.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bubble);

        bubbleTalkSQLite= new BubbleTalkSQLite(this);

        //Shared pref
        shre = PreferenceManager.getDefaultSharedPreferences(this);

        Intent myIntent = getIntent();
        idBubble = myIntent.getStringExtra("id");
        bubble = bubbleTalkSQLite.getOneBubble(idBubble);

        editTextMyBubbleName = (EditText) findViewById(R.id.editTextMyBubbleName);
        editTextMyBubbleName.setText(bubble.getName());

        editTextMyBubbleDescription = (EditText) findViewById(R.id.editTextMyBubbleDescription);
        editTextMyBubbleDescription.setText(bubble.getDescription());

        avatarBefore = Base64.decode(shre.getString("bubble_"+bubble.getId(),""), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBefore,0,avatarBefore.length);
        imageView = (ImageView) findViewById(R.id.imageViewMyBubble);
        imageView.setImageBitmap(bitmap);


        Button buttonUpdateAvatar = (Button) findViewById(R.id.buttonUpdateAvatarMyBubble);
        buttonUpdateAvatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });

        Button buttonSave = (Button) findViewById(R.id.buttonSaveMyBubble);
        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                updateBubble();
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

        if(!bubble.getName().equals(editTextMyBubbleName.getText().toString())){
            if(editTextMyBubbleName.length()>2 || editTextMyBubbleName.length()==0) {
                name = editTextMyBubbleName.getText().toString();
                bubble.setName(name);
                database.getReference("bubble").child(idBubble).child("name").setValue(name);
                testUpdate = true;
            }else{
                Toast.makeText(this, "Votre nom de Bubble doit faire au moins 3 caractères !",Toast.LENGTH_SHORT).show();
            }
        }
/*
        if(usePseudoBefore!=checkBox.isChecked()){
            if(editTextPseudo.length()==0) {
                usePseudoStr = "false";
                usePseudo = false;
                currentUser.setUsePseudo(usePseudo);
                database.getReference("User").child(user.getUid()).child("usePseudo").setValue(usePseudo);
                testUpdate = true;
            }else if(editTextPseudo.length()>2 ) {
                if (checkBox.isChecked()) {
                    usePseudoStr = "true";
                    usePseudo = true;
                } else {
                    usePseudoStr = "false";
                    usePseudo = false;
                }

                currentUser.setUsePseudo(usePseudo);
                database.getReference("User").child(user.getUid()).child("usePseudo").setValue(usePseudo);
                usePseudoBefore = checkBox.isChecked();
            }else{
                Toast.makeText(ParamActivity.this, "Votre Pseudo doit faire au moins 3 caractères pour pouvoir être utilisé!",Toast.LENGTH_SHORT).show();
            }
        }

        if(!avatarBefore.equals(avatarDisplay)){
            avatar = "true";
            currentUser.setAvatar(avatar);
            avatarBefore = avatarDisplay;
            updateFirebaseAndPreferenceStorage();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(avatarDisplay);
                byte[] hash = md.digest();
                database.getReference("User").child(user.getUid()).child("md5Avatar").setValue(returnHex(hash));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            testUpdate = true;
        }

        if(testUpdate == true) {
            bubbleTalkSQLite.updateUser(user.getUid(), new String[]{pseudo, "", "", usePseudoStr, avatar});
            Toast.makeText(this, "Modification sauvegardée !", Toast.LENGTH_SHORT).show();
        }*/
    }

}
