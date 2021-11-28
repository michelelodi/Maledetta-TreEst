package it.unimi.maledettatreest.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import it.unimi.maledettatreest.MainActivity;

public class ImageManager {

    private static final String TAG = MainActivity.TAG_BASE + "ImageManager";

    public static Bitmap base64ToBitmap(String encodedImage) {
        Log.d(TAG,"Converting from base64 to bitmap");
        if(org.apache.commons.codec.binary.Base64.isBase64(encodedImage)) {
            byte[] decodedString = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        else return null;
    }

    public static String bitmapToBase64(Bitmap bm) {
        Log.d(TAG,"Converting from bitmap to base64");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
