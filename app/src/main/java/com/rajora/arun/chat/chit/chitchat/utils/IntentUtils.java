package com.rajora.arun.chat.chit.chitchat.utils;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder;
import com.rajora.arun.chat.chit.chitchat.R.array;
import com.rajora.arun.chat.chit.chitchat.R.string;

import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.CATEGORY_OPENABLE;

public class IntentUtils {
	public static void fireFilePickerIntent(Fragment fragment, int requestCode) {
		Intent pickFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickFileIntent.setType("*/*");
		pickFileIntent.addCategory(CATEGORY_OPENABLE);
		pickFileIntent = Intent.createChooser(pickFileIntent, "Choose a file");
		if (pickFileIntent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
			fragment.startActivityForResult(Intent.createChooser(pickFileIntent, "Pick a file"), requestCode);
		} else {
			Toast.makeText(fragment.getContext(), string.cc_warn_no_picker, Toast.LENGTH_SHORT).show();
		}
	}

	public static void fireLocationPickerIntent(Fragment fragment, int requestCode) {
		IntentBuilder builder = new IntentBuilder();
		try {
			fragment.startActivityForResult(builder.build(fragment.getActivity()), requestCode);
		} catch (GooglePlayServicesRepairableException e) {
			Toast.makeText(fragment.getContext(), string.cc_twy_later, Toast.LENGTH_SHORT).show();
		} catch (GooglePlayServicesNotAvailableException e) {
			Toast.makeText(fragment.getContext(), string.cc_install_gps, Toast.LENGTH_SHORT).show();
		}
	}

	public static void fireContactPickerIntent(Fragment fragment, int requestCode) {
		Intent pickContactIntent = new Intent(ACTION_PICK, Phone.CONTENT_URI);
		if (pickContactIntent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
			fragment.startActivityForResult(pickContactIntent, requestCode);
		} else {
			Toast.makeText(fragment.getContext(), string.cc_no_cpicker, Toast.LENGTH_SHORT).show();
		}
	}


	public static void showVideoSelectionPopup(final Fragment fragment, final int requestCodeCapture, final int requestCodePick) {
		new Builder(fragment.getContext()).setTitle(string.cc_pick_video_from)
				.setItems(array.pick_video_option_list_array, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
							if (takePictureIntent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
								fragment.startActivityForResult(takePictureIntent, requestCodeCapture);
							} else {
								Toast.makeText(fragment.getContext(), string.cc_no_camera_app, Toast.LENGTH_SHORT).show();
							}
						} else if (which == 1) {
							Intent PickImageintent = new Intent().setType("video/*").setAction(Intent.ACTION_GET_CONTENT);
							if (PickImageintent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
								fragment.startActivityForResult(PickImageintent, requestCodePick);
							} else {
								Toast.makeText(fragment.getContext(), string.cc_no_video_picker, Toast.LENGTH_SHORT).show();
							}
						}
					}
				}).create().show();
	}
}
