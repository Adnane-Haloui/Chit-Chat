package com.rajora.arun.chat.chit.chitchat.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

/**
 * Created by arc on 20/1/17.
 */

public class FileUtils {

	public static String getPath(final Context context, final Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
			if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
				final String[] ids = DocumentsContract.getDocumentId(uri).split(":");
				final String type = ids[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + ids[1];
				}
			}
			else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
				final Uri contentUri = ContentUris
						.withAppendedId(Uri.parse("content://downloads/public_downloads"),
								Long.valueOf(DocumentsContract.getDocumentId(uri)));
				return getDataColumn(context, contentUri, null, null);
			}
			else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
				final String[] ids = DocumentsContract.getDocumentId(uri).split(":");
				final String type = ids[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				return getDataColumn(context, contentUri, "_id=?", new String[]{ids[1]});
			}
		}
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			if ("com.google.android.apps.photos.content".equals(uri.getAuthority()))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public static String getMimeType(String uri) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}

	private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(cursor.getColumnIndexOrThrow("_data"));
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
}
