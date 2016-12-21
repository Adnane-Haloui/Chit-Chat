package com.rajora.arun.chat.chit.chitchat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by arc on 14/10/16.
 */

public class ImageUtils {
    public static String bitmapToString(Bitmap bitmap)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String base64Image) {
        byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
