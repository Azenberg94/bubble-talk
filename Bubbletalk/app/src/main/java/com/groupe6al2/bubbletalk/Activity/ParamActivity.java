package com.groupe6al2.bubbletalk.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.User;
import com.groupe6al2.bubbletalk.R;

public class ParamActivity extends AppCompatActivity {

    TextView textViewName;
    FirebaseUser user;
    FirebaseAuth auth;

    BubbleTalkSQLite bubbleTalkSQLite;
    User currentUser;

    String pseudoBefore;
    boolean usePseudoBefore;
    String avatarBefore;

    EditText editTextPseudo;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);


        //Firebase user
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        //Local user
        bubbleTalkSQLite= new BubbleTalkSQLite(this);
        currentUser =  bubbleTalkSQLite.getUser(user.getUid());
        pseudoBefore = currentUser.getPseudo();
        usePseudoBefore = currentUser.getUsePseudo();
        avatarBefore = currentUser.getAvatar();

        //initialize component
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText(currentUser.getName());

        editTextPseudo = (EditText) findViewById(R.id.editTextPseudo);
        editTextPseudo.setText(currentUser.getPseudo());

        checkBox = (CheckBox) findViewById(R.id.checkBoxPseudo);
        System.out.println("usePseudo : " + currentUser.getUsePseudo());
        if(currentUser.getUsePseudo()==true){
            checkBox.setChecked(true);
        }

        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               saveProfile();
            }
        });


    }

    public void saveProfile(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String pseudo = "";
        Boolean usePseudo;
        String usePseudoStr = "";
        String avatar = "";

        if(!pseudoBefore.equals(editTextPseudo.getText().toString())){
            if(editTextPseudo.length()>2 || editTextPseudo.length()==0) {

                if(editTextPseudo.length()==0){
                    checkBox.setChecked(false);
                }
                pseudo = editTextPseudo.getText().toString();
                currentUser.setPseudo(pseudo);
                database.getReference("User").child(user.getUid()).child("pseudo").setValue(pseudo);
                pseudoBefore =  editTextPseudo.getText().toString();
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
            }else if(editTextPseudo.length()>2) {
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

        bubbleTalkSQLite.updateUser(user.getUid(),new String[]{pseudo,"","",avatar,usePseudoStr});
        Toast.makeText(ParamActivity.this, "Modification sauvegardée !", Toast.LENGTH_SHORT).show();
    }
}
