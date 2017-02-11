package com.groupe6al2.bubbletalk.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.User;
import com.groupe6al2.bubbletalk.R;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.groupe6al2.bubbletalk.Class.Utils.readBytesFromFile;
import static com.groupe6al2.bubbletalk.Class.Utils.returnHex;

public class ParamActivity extends AppCompatActivity {

    TextView textViewName;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");

    SharedPreferences shre;

    BubbleTalkSQLite bubbleTalkSQLite;
    User currentUser;

    String pseudoBefore;
    boolean usePseudoBefore;
    byte[] avatarBefore =  new byte[0];
    byte[] avatarDisplay = new byte[0];
    EditText editTextPseudo;
    CheckBox checkBox;
    ImageView imageView;

    String myAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        //Shared pref
        shre = PreferenceManager.getDefaultSharedPreferences(this);

        //Firebase user
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        myAvatar = user.getUid()+".jpg";

        //Local user
        bubbleTalkSQLite= new BubbleTalkSQLite(this);
        currentUser =  bubbleTalkSQLite.getUser(user.getUid());
        pseudoBefore = currentUser.getPseudo();
        usePseudoBefore = currentUser.getUsePseudo();

        //initialize component
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText(currentUser.getName());

        editTextPseudo = (EditText) findViewById(R.id.editTextPseudo);
        editTextPseudo.setText(currentUser.getPseudo());

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(null);
        checkBox = (CheckBox) findViewById(R.id.checkBoxPseudo);
        //System.out.println("usePseudo : " + currentUser.getUsePseudo());
        if(currentUser.getUsePseudo()==true){
            checkBox.setChecked(true);
        }


       /* if(savedInstanceState != null)
        {
            avatarDisplay = savedInstanceState.getByteArray("myAvatar");
        }
        if (avatarDisplay.length>0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarDisplay,0,avatarDisplay.length);
            imageView.setImageBitmap(bitmap);
        } else */if(!shre.getString("avatar_"+user.getUid(),"").equals("")){
            avatarBefore = Base64.decode(shre.getString("avatar_"+user.getUid(),""), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBefore,0,avatarBefore.length);
            imageView.setImageBitmap(bitmap);
        }

        Button buttonUpdateAvatar = (Button) findViewById(R.id.buttonUpadteAvatar);
        buttonUpdateAvatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });

        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveProfile();
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
            System.out.println("BEFORE : " + avatarDisplay.length);
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


    public void saveProfile(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String pseudo = pseudoBefore;
        Boolean usePseudo = usePseudoBefore;
        String usePseudoStr = "";
        String avatar = "";
        boolean testUpdate = false;

        if(!pseudoBefore.equals(editTextPseudo.getText().toString())){
            if(editTextPseudo.length()>2 || editTextPseudo.length()==0) {

                if(editTextPseudo.length()==0){
                    checkBox.setChecked(false);
                }
                pseudo = editTextPseudo.getText().toString();
                currentUser.setPseudo(pseudo);
                database.getReference("User").child(user.getUid()).child("pseudo").setValue(pseudo);
                pseudoBefore =  editTextPseudo.getText().toString();
                testUpdate = true;
            }else{
                Toast.makeText(ParamActivity.this, "Votre Pseudo doit faire au moins 3 caractères !",Toast.LENGTH_SHORT).show();
            }
        }

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
            Toast.makeText(ParamActivity.this, "Modification sauvegardée !", Toast.LENGTH_SHORT).show();
        }
    }



    public void updateFirebaseAndPreferenceStorage(){

        String encodedImage = Base64.encodeToString(avatarDisplay, Base64.DEFAULT);
        SharedPreferences.Editor edit=shre.edit();
        edit.putString("avatar_"+user.getUid(),encodedImage);
        edit.commit();


        // Points to the root reference

        StorageReference storageRef = storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");
        StorageReference avatarFileRef = storageRef.child("avatar/"+myAvatar);


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

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putByteArray("myAvatar", avatarDisplay);
    }

}
