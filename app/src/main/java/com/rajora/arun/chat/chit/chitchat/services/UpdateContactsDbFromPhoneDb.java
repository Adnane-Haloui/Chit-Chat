package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ProviderHelper;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.utils.FirebaseUtils;
import com.rajora.arun.chat.chit.chitchat.utils.Utils;

public class UpdateContactsDbFromPhoneDb extends IntentService {

	public static String BROADCAST_ACTION = "com.rajora.arun.chat.chit.chitchat.updateContacts.BROADCAST";
	public static String WORK_STATUS = "com.rajora.arun.chat.chit.chitchat.updateContacts.WORK_STATUS";

	public static boolean isProcessing;
	FirebaseDatabase firebaseDatabase;

	public UpdateContactsDbFromPhoneDb() {
		super("updateContactsDbFromPhoneDb");
		isProcessing = false;
		firebaseDatabase = FirebaseDatabase.getInstance();
	}

	public static void startContactDbUpdate(Context context) {
		Intent intent = new Intent(context, UpdateContactsDbFromPhoneDb.class);
		context.startService(intent);
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		isProcessing = true;
		String myPhNo = getSharedPreferences("user-details", MODE_PRIVATE).getString("phone", "bad-data");
		Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, new String[]{
						Phone.NORMALIZED_NUMBER,
						Phone._ID,
						Phone.PHOTO_URI,
						Phone.DISPLAY_NAME_PRIMARY},
				Phone.NORMALIZED_NUMBER + " IS NOT NULL ", null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME_PRIMARY));
				final String ph_no = Utils.convertPhoneNumberToE164(this,
						cursor.getString(cursor.getColumnIndex(Phone.NORMALIZED_NUMBER)));
				if (myPhNo.equals(ph_no)) {
					continue;
				}
				String imgUri = cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI));
				Cursor presentValueCursor = getContentResolver().query(ChatContentProvider.CONTACT_LIST_URI, new String[]{
								ContractContacts.COLUMN_CONTACT_ID,
								ContractContacts.COLUMN_IS_USER},
						ContractContacts.COLUMN_CONTACT_ID + " = ? AND " + ContractContacts.COLUMN_IS_BOT + " = ?",
						new String[]{ph_no, "0"}, null);
				if (Utils.canCursorMoveToFirst(presentValueCursor)) {
					ProviderHelper.updateContact(this, name, ph_no,
							presentValueCursor.getInt(presentValueCursor.getColumnIndex(
									ContractContacts.COLUMN_IS_USER)) != 0,
							imgUri);
				} else {
					ProviderHelper.addContact(this, name, ph_no, false, imgUri);
				}
				if (presentValueCursor != null)
					presentValueCursor.close();
				FirebaseUtils.getReferenceForUser(firebaseDatabase, ph_no)
						.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								if (dataSnapshot != null && dataSnapshot.exists()) {
									String about = dataSnapshot.child("about").exists() ? dataSnapshot.child("about").getValue().toString() : "";
									ProviderHelper.updateContact(UpdateContactsDbFromPhoneDb.this, about, ph_no, true);
								}
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {
							}
						});
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		isProcessing = false;
		Intent localIntent = new Intent(BROADCAST_ACTION).putExtra(WORK_STATUS, "done");
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

}
