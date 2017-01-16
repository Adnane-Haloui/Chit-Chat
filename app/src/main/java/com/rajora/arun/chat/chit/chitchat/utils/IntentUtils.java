package com.rajora.arun.chat.chit.chitchat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.rajora.arun.chat.chit.chitchat.R;

import static android.content.Intent.ACTION_PICK;

/**
 * Created by arc on 16/1/17.
 */

public class IntentUtils {
	public static void fireFilePickerIntent(Context context,int requestCode){
		Intent pickFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickFileIntent.setType("*/*");
		pickFileIntent = Intent.createChooser(pickFileIntent, "Choose a file");
		if (pickFileIntent.resolveActivity(context.getPackageManager()) != null) {
			((Activity) context).startActivityForResult(pickFileIntent, requestCode);
		}
		else{
			Toast.makeText(context,"No file picker app found!",Toast.LENGTH_SHORT).show();
		}
	}

	public static void fireLocationPickerIntent(final Context context,Activity activity,int requestCode){
		PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
		try {
			((Activity) context).startActivityForResult(builder.build(activity), requestCode);
		} catch (GooglePlayServicesRepairableException e) {
			Toast.makeText(context,"Please try again later!",Toast.LENGTH_SHORT).show();
		} catch (GooglePlayServicesNotAvailableException e) {
			Toast.makeText(context,"Please install google play services",Toast.LENGTH_SHORT).show();
		}
	}
	public static void fireContactPickerIntent(Context context,int requestCode){
		Intent pickContactIntent = new Intent(ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		if (pickContactIntent.resolveActivity(context.getPackageManager()) != null) {
			((Activity) context).startActivityForResult(pickContactIntent,requestCode);
		}
		else{
			Toast.makeText(context,"No contact picker app found!",Toast.LENGTH_SHORT).show();
		}
	}

	public static void showImageSelectionPopup(final Context context, final int requestCodeCapture, final int requestCodePick){
		new AlertDialog.Builder(context).setTitle(R.string.pick_image_from)
				.setItems(R.array.pick_image_option_list_array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
								((Activity) context).startActivityForResult(takePictureIntent,requestCodeCapture);
							}
							else{
								Toast.makeText(context,"No camera app found!",Toast.LENGTH_SHORT).show();
							}
						}
						else if(which==1){
							Intent PickImageintent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
							if (PickImageintent.resolveActivity(context.getPackageManager()) != null) {
								((Activity) context).startActivityForResult(PickImageintent,requestCodePick);
								((Activity) context).startActivityForResult(PickImageintent,requestCodePick);
							}
							else{
								Toast.makeText(context,"No app found to pick image!",Toast.LENGTH_SHORT).show();
							}
						}
					}
				}).create().show();
	}

	public static void showVideoSelectionPopup(final Context context, final int requestCodeCapture, final int requestCodePick){
		new AlertDialog.Builder(context).setTitle("Pick video from")
				.setItems(R.array.pick_video_option_list_array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
							if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
								((Activity) context).startActivityForResult(takePictureIntent,requestCodeCapture);
							}
							else{
								Toast.makeText(context,"No camera app found!",Toast.LENGTH_SHORT).show();
							}
						}
						else if(which==1){
							Intent PickImageintent = new Intent().setType("video/*").setAction(Intent.ACTION_GET_CONTENT);
							if (PickImageintent.resolveActivity(context.getPackageManager()) != null) {
								((Activity) context).startActivityForResult(PickImageintent,requestCodePick);
							}
							else{
								Toast.makeText(context,"No app found to pick video!",Toast.LENGTH_SHORT).show();
							}
						}
					}
				}).create().show();
	}
}
