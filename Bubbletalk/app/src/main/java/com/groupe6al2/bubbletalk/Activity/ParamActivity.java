package com.groupe6al2.bubbletalk.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.groupe6al2.bubbletalk.Class.BubbleTalkSQLite;
import com.groupe6al2.bubbletalk.Class.User;
import com.groupe6al2.bubbletalk.R;

public class ParamActivity extends AppCompatActivity {

    TextView textViewName;
    FirebaseUser users;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        users= auth.getCurrentUser();

        BubbleTalkSQLite bubbleTalkSQLite= new BubbleTalkSQLite(this);

        User currentUser =  bubbleTalkSQLite.getUser(users.getUid());

        System.out.println("Mon UID : " + users.getUid());

        textViewName= (TextView) findViewById(R.id.textViewName);
        textViewName.setText(currentUser.getName());

        CheckBox checkBox =(CheckBox) findViewById(R.id.checkBoxPseudo);
        if(currentUser.getUsePseudo()==true) {
            Log.i("USE_LOGIN", "CHECKED");
        }else{
            Log.i("USE_LOGIN", "NOTCHECKED");
        }


        if(currentUser.getUsePseudo()==true){
            checkBox.setChecked(true);
        }

        if(currentUser.getUsePseudo()==true){

        }


    }
}
