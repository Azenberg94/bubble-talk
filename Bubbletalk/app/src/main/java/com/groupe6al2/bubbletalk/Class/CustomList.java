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

import com.groupe6al2.bubbletalk.R;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] web;
    private final String[] image;
    public CustomList(Activity context,
                      String[] web, String[] image) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.image = image;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);

        byte[] b = Base64.decode(image[position], Base64.DEFAULT);
        imageView.setImageBitmap(null);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        imageView.setImageBitmap(bitmap);


        return rowView;
    }
}