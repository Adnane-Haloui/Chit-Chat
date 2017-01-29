package com.rajora.arun.chat.chit.chitchatdevelopers.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageUtils {
	public static String bitmapArrayToString(byte[] bitmapArray) {
		return Base64.encodeToString(bitmapArray, Base64.DEFAULT);
	}

	public static Bitmap stringToBitmap(String base64Image) {
		byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
	}

	public static void loadBitmapFromFirebase(Context context, String url, int placeholderId, ImageView target) {
		Glide.with(context)
				.using(new FirebaseImageLoader())
				.load(FirebaseStorage.getInstance().getReference(url))
				.diskCacheStrategy(DiskCacheStrategy.RESULT)
				.centerCrop()
				.placeholder(placeholderId)
				.error(placeholderId)
				.crossFade()
				.into(target);
	}


	private static int getPowerOfTwoForSampling(int ratio) {
		return Integer.highestOneBit(ratio) == 0 ? 1 : Integer.highestOneBit(ratio);
	}

	public static byte[] getCompressedImageArray(Context context, Uri uri) {

		try {
			InputStream input = context.getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			if (input != null) {
				input.close();
			}
			if (options.outWidth == -1 || options.outHeight == -1)
				return null;

			int originalSize = options.outHeight > options.outWidth ? options.outHeight : options.outWidth;

			double ratio = originalSize > 256 ? originalSize / 256 : 1.0;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = getPowerOfTwoForSampling((int) ratio);
			input = context.getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.WEBP, 90, byteArrayOutputStream);
			bitmap.recycle();
			input.close();
			byte[] imgArray = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			return imgArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
