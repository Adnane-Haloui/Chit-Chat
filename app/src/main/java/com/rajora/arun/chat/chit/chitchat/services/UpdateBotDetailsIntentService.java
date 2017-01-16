package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;

import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;

public class UpdateBotDetailsIntentService extends IntentService {

	private static final String EXTRA_BOT_DETAILS =
			"com.rajora.arun.chat.chit.chitchat.services.extra.BOT_DETAILS";

	public UpdateBotDetailsIntentService() {
		super("UpdateBotDetailsIntentService");
	}

	public static void startBotDetailsUpdate(Context context, FirebaseBotsDataModel botDetails) {
		Intent intent = new Intent(context, UpdateBotDetailsIntentService.class);
		intent.putExtra(EXTRA_BOT_DETAILS, botDetails);
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			FirebaseBotsDataModel botDetails=intent.getParcelableExtra(EXTRA_BOT_DETAILS);
			if(botDetails!=null){
				Cursor currendDetailsCursor=getContentResolver().query(ChatContentProvider.CONTACT_LIST_URI,
						new String[]{ContractContacts._ID},
						ContractContacts.COLUMN_CONTACT_ID+" = ? AND "+ContractContacts.COLUMN_IS_BOT+" = ? ",
						new String[]{botDetails.getGid(),"1"},null);
				ContentValues contentValues=new ContentValues();
				contentValues.put(ContractContacts.COLUMN_CONTACT_ID,botDetails.getGid());
				contentValues.put(ContractContacts.COLUMN_IS_BOT,true);
				contentValues.put(ContractContacts.COLUMN_NAME,botDetails.getName());
				contentValues.put(ContractContacts.COLUMN_ABOUT,botDetails.getDesc());
				contentValues.put(ContractContacts.COLUMN_DEV_NAME,botDetails.getDev_name());
				contentValues.put(ContractContacts.COLUMN_IS_USER, botDetails.is_deleted());

				if(currendDetailsCursor!=null && currendDetailsCursor.getCount()>0){
					getContentResolver().update(ChatContentProvider.CONTACT_LIST_URI,contentValues,
							ContractContacts.COLUMN_CONTACT_ID+" = ? AND "+ContractContacts.COLUMN_IS_BOT+" = ? ",
							new String[]{botDetails.getGid(),"1"});
				}
				else{
					getContentResolver().insert(ChatContentProvider.CONTACT_LIST_URI,contentValues);
				}
				if(currendDetailsCursor!=null){
					currendDetailsCursor.close();
				}
			}
		}
	}

}
