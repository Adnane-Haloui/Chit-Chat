package com.rajora.arun.chat.chit.chitchat.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.google.android.gms.location.places.Place;
import com.google.gson.JsonObject;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.services.SendMessageService;

import java.io.File;

/**
 * Created by arc on 16/1/17.
 */

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

	public static void sendFileDetails(Context context,String ph_no,ContactItemDataModel contactData,Uri uri, String type){
		File file=new File(uri.getPath());
		JsonObject fileDetails=new JsonObject();
		fileDetails.addProperty("name",file.getName());
		fileDetails.addProperty("length",file.length()/1024);
		SendMessageService.startSendFileMessage(context,ph_no,
				contactData.getContact_id(),
				uri.getPath(),fileDetails.toString(),type,contactData.is_bot(),utils.getCurrentTimestamp());
	}
	public static void sendContactDetails(Context context,String ph_no,ContactItemDataModel contactData,Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if(cursor!=null && cursor.moveToFirst()){
			String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
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

}
