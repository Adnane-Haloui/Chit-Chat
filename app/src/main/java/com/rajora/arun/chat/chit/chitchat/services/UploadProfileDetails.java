package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;

import java.util.HashMap;
import java.util.List;

public class UploadProfileDetails extends IntentService {

	public static final String PARAM_PIC_URI = "PROFILE_PIC_URI";
	public static final String PARAM_NAME = "NAME";
	public static final String PARAM_ABOUT = "ABOUT";

	public UploadProfileDetails() {
		super("UploadProfileDetails");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Uri picUri = null;
			if (extras.getString(PARAM_PIC_URI) != null) {
				picUri = Uri.parse(extras.getString(PARAM_PIC_URI));
			}
			final String name = extras.getString(PARAM_NAME);
			final String about = extras.getString(PARAM_ABOUT);


			FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
			FirebaseStorage mStorage = FirebaseStorage.getInstance();
			SharedPreferences sharedPreferences = getSharedPreferences("user-details", MODE_PRIVATE);
			String ph_no = sharedPreferences.getString("phone", "bad-data").substring(1);


			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("name", name);
			editor.putString("about", about);
			editor.commit();

			HashMap<String, Object> values = new HashMap<String, Object>();
			values.put("name", name);
			values.put("about", about);
			DatabaseReference databaseReference = mFirebaseDatabase.
					getReference("users/" + ph_no);
			databaseReference.updateChildren(values);
			if (picUri != null) {
				final byte[] imgArray = ImageUtils.getCompressedImageArray(this, picUri);
				if (imgArray != null) {
					editor.putString("profile_pic", ImageUtils.bitmapArrayToString(imgArray));
					editor.commit();
					StorageReference ProfilePicRef = mStorage.getReference().child(ph_no + "/profile/profilepic.webp");
					if (picUri != null) {
						List<UploadTask> tasks = ProfilePicRef.getActiveUploadTasks();
						if (!tasks.isEmpty()) {
							for (UploadTask task : tasks) {
								task.cancel();
							}
						}
						ProfilePicRef.putBytes(imgArray);
					}
				}
			}

		}
	}
}