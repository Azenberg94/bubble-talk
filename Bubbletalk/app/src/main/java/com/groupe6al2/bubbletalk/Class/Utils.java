package com.groupe6al2.bubbletalk.Class;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by goasguenl on 07/02/2017.
 */

public class Utils {

    double longitude;
    double latitude;

    public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

    public static String returnHex(byte[] inBytes) throws Exception {
        String hexString = "";
        for (int i = 0; i < inBytes.length; i++) { //for loop ID:1
            hexString +=
                    Integer.toString((inBytes[i] & 0xff) + 0x100, 16).substring(1);
        }                                   // Belongs to for loop ID:1
        return hexString;
    }
}
