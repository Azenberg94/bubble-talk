package com.groupe6al2.bubbletalk.Class;

/**
 * Created by Loïc on 13/02/2017.
 */


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

public class CustomList extends ArrayAdapter<String>{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef= storage.getReferenceFromUrl("gs://bubbletalk-967fa.appspot.com");;
    private final Activity context;
    private final String[] web;
    private final String[] image;
    private final String[] id;
    public CustomList(Activity context,
                      String[] web, String[] image, String[] id) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.image = image;
        this.id = id;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);


        if(image[position].equals("")){
            imageView.setImageResource(R.drawable.defaut_room);
        }else if(image[position].equals("myGlideFirebase")){
            // Create a reference with an initial file path and name
            StorageReference pathReference = storageRef.child("bubble/"+id[position]+"");

            // Load the image using Glide

            Glide.with(rowView.getContext())
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .into(imageView);

        }else{
            byte[] b = Base64.decode(image[position], Base64.DEFAULT);
            imageView.setImageBitmap(null);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageView.setImageBitmap(bitmap);
        }


        return rowView;
    }
}