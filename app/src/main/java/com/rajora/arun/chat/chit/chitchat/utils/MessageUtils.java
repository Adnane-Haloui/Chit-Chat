package com.rajora.arun.chat.chit.chitchat.utils;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.gson.JsonObject;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.services.FirebaseFileUploadService;
import com.rajora.arun.chat.chit.chitchat.services.SendMessageService;

import java.io.File;


public class MessageUtils {
	public static void sendLocationDetails(Context context, String ph_no, ContactItemDataModel contactData, Place place) {
		if(place!=null){
			double latitude = place.getLatLng().latitude;
			double longitude= place.getLatLng().longitude;
			String name = place.getName()==null?"":String.valueOf(place.getName());
			JsonObject location_message=new JsonObject();
			location_message.addProperty("latitude",latitude);
			location_message.addProperty("longitude",longitude);
			location_message.addProperty("name",name);
			SendMessageService.startSendTextMessage(context,ph_no,
					contactData.getContact_id(),
					location_message.toString(),
					"location", contactData.is_bot(),utils.getCurrentTimestamp());
		}
	}

	public static void sendContactDetails(Context context,String ph_no,ContactItemDataModel contactData,Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if(cursor!=null && cursor.moveToFirst()){
			String phoneNo = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
			String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
			if (phoneNo == null) phoneNo = "";
			if (name == null) name = "";
			JsonObject contact_message=new JsonObject();
			contact_message.addProperty("name",name);
			contact_message.addProperty("number",phoneNo);
			SendMessageService.startSendTextMessage(context,ph_no,
					contactData.getContact_id(),
					contact_message.toString(),
					"contact", contactData.is_bot(),utils.getCurrentTimestamp());
		}
	}

	public static void sendFileDetails(Context context,String ph_no,ContactItemDataModel contactData,Uri uri, String type){
		String path=getPath(context,uri);
		if(path==null) return;
		String displayName=null;
		long size=0;
		if (uri.toString().startsWith("content://")) {
			Cursor mCursor = null;
			try {
				mCursor = context.getContentResolver().query(uri, null, null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					displayName = mCursor.getString(mCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
					if(!mCursor.isNull(mCursor.getColumnIndex(OpenableColumns.SIZE)))
						size=mCursor.getInt(mCursor.getColumnIndex(OpenableColumns.SIZE));
				}
			} finally {
				if(mCursor!=null && !mCursor.isClosed())
					mCursor.close();
			}
		} else if (uri.toString().startsWith("file://")) {
			File myFile = new File(uri.getPath());
			displayName = myFile.getName();
			size=myFile.length();
		}
		if(displayName==null) displayName="Unknown";

		JsonObject fileDetails=new JsonObject();
		fileDetails.addProperty("name",displayName);
		fileDetails.addProperty("size",size);
		if(size/(1024*1024)>100){
			Toast.makeText(context, R.string.cc_file_size_error, Toast.LENGTH_SHORT).show();
		}
		else{
			FirebaseFileUploadService.startSendFileMessage(context,ph_no,
					contactData.getContact_id(),displayName,
					uri.toString(),fileDetails.toString(),
					type,contactData.is_bot(),
					utils.getCurrentTimestamp());
		}
	}

	private static String getPath(Context context,Uri uri){
		String path = null;
		String[] projection = { FileColumns.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		if(cursor == null) path = uri.getPath();
		else{
			cursor.moveToFirst();
			int column_index = cursor.getColumnIndexOrThrow(projection[0]);
			path = cursor.getString(column_index);
			cursor.close();
		}
		return path == null || path.isEmpty() ? uri.getPath() : path;
	}
}
