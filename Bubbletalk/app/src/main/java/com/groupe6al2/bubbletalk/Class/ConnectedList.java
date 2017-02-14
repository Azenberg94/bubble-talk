package com.groupe6al2.bubbletalk.Class;

/**
 * Created by Loïc on 13/02/2017.
 */


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.groupe6al2.bubbletalk.R;

public class ConnectedList extends ArrayAdapter<String>{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef= storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");;
    private final Activity context;
    private final String[] name;
    private final String[] id;
    public ConnectedList(Activity context,
                         String[] name, String[] id) {
        super(context, R.layout.list_single, name);
        this.context = context;
        this.name = name;
        this.id = id;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(name[position]);

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("avatar/"+id[position]+".jpg");
        // Load the image using Glide
        Glide.with(rowView.getContext())
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(imageView);

        return rowView;
    }
}