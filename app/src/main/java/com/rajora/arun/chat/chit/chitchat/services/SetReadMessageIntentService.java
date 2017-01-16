package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;

import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;

public class SetReadMessageIntentService extends IntentService {

	private static final String EXTRA_CONTACT_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTACT_ID";
	private static final String EXTRA_IS_BOT = "com.rajora.arun.chat.chit.chitchat.services.extra.IS_BOT";

	public SetReadMessageIntentService() {
		super("SetReadMessageIntentService");
	}

	public static void setMessageRead(Context context, String contact_id, boolean is_bot) {
		Intent intent = new Intent(context, SetReadMessageIntentService.class);
		intent.putExtra(EXTRA_CONTACT_ID, contact_id);
		intent.putExtra(EXTRA_IS_BOT, is_bot);
		context.startService(intent);
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String contact_id = intent.getStringExtra(EXTRA_CONTACT_ID);
			final boolean is_bot = intent.getBooleanExtra(EXTRA_IS_BOT,false);
			ContentValues contentValues=new ContentValues();
			contentValues.put(ContractChat.COLUMN_MESSAGE_STATUS,"read");
			getContentResolver().update(ChatContentProvider.CHAT_URI,
					contentValues,
					ContractChat.COLUMN_CONTACT_ID+" = ? AND "+ContractChat.COLUMN_IS_BOT+" = ? ",
					new String[]{contact_id,is_bot?"1":"0"});
		}
	}


}
