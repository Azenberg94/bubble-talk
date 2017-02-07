package com.groupe6al2.bubbletalk.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.groupe6al2.bubbletalk.Class.Utils;
import com.groupe6al2.bubbletalk.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bubble);

        manager = (LocationManager) getSystemService( this.LOCATION_SERVICE );
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
            bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream);
            avatarBubbleDisplay = stream.toByteArray();

            imageView.setImageBitmap(bitmap);


        }
    }

    public void createBubble(){
        if(editTextNameCreateBubble.getText().toString().equals("")){
            Toast.makeText(CreateBubbleActivity.this, "Veuillez saisir un nom !",Toast.LENGTH_SHORT).show();
        }else if(editTextNameCreateBubble.getText().toString().length()<3 || editTextNameCreateBubble.getText().toString().length()>50 ){
            Toast.makeText(CreateBubbleActivity.this, "Le nom de bulle doit faire entre 3 et 50 caractères pour pouvoir être utilisé !",Toast.LENGTH_SHORT).show();
        }else if (editTextDescriptionCreateBubble.getText().toString().length()>250) {
            Toast.makeText(CreateBubbleActivity.this, "La description ne peut dépasser les 250 caractères !",Toast.LENGTH_SHORT).show();
        }else if( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER ) || network==null || !network.isConnected()) {
            Toast.makeText(CreateBubbleActivity.this, "Veuillez verifiez votre connexion internet et activer votre géolocalisation !",Toast.LENGTH_SHORT).show();
        }else{
            Double[] location  = this.getCurrentLocation(manager);

            System.out.println("longitude " +location[0].toString());
            System.out.println("latitude " +location[1].toString());
        }
    }


    public Double[] getCurrentLocation(LocationManager locationManager) {

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        String provider = locationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);

        ArrayList<String> names = (ArrayList<String>) locationManager.getAllProviders();

        //Location location = new Location(provider);
        //Location lastKnownLocation = new Location(provider);
        Location location;
        location = new Location(provider);
        locationManager.getLastKnownLocation(provider);
        if(location!=null){
            longitude=location.getLongitude();
            latitude=location.getLatitude();

        }

        return new Double[]{longitude, latitude};
    }
}
