package com.groupe6al2.bubbletalk.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.groupe6al2.bubbletalk.Fragment.ListBubbleFragment;
import com.groupe6al2.bubbletalk.Fragment.RoomBubbleFragment;
import com.groupe6al2.bubbletalk.R;

public class BubbleActivity extends AppCompatActivity {

    ListBubbleFragment listBubbleFragment;
    RoomBubbleFragment roomBubbleFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        listBubbleFragment = new ListBubbleFragment();
        roomBubbleFragment = new RoomBubbleFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, listBubbleFragment);
        fragmentTransaction.commit();

    }


    public void switchFragment(Fragment fragment){
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


}
