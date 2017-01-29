package com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.rajora.arun.chat.chit.chitchatdevelopers.dataModel.LocalBotDataModel;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;

/**
 * Created by arc on 19/10/16.
 */

public class BotProviderHelper {

	public static Uri AddBot(Context context, LocalBotDataModel item) {
		if (!botExists(context, item.Gid)) {
			ContentValues values = new ContentValues();
			values.put(BotContracts.COLUMN_ID, item.id);
			values.put(BotContracts.COLUMN_BOT_NAME, item.name);
			values.put(BotContracts.COLUMN_ABOUT, item.desc);
			values.put(BotContracts.COLUMN_API_ENDPOINT, item.endpoint);
			values.put(BotContracts.COLUMN_SECRET, item.secret);
			values.put(BotContracts.COLUMN_GLOBAL_ID, item.Gid);
			values.put(BotContracts.COLUMN_TIMESTAMP, item.timestamp);
			values.put(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP, item.image_last_update_timestamp);
			return context.getContentResolver().insert(BotContentProvider.CONTENT_URI, values);
		}
		return null;
	}

	public static Uri AddBot(Context context, LocalBotDataModel item, String pic) {
		if (!botExists(context, item.Gid)) {
			ContentValues values = new ContentValues();
			values.put(BotContracts.COLUMN_ID, item.id);
			values.put(BotContracts.COLUMN_BOT_NAME, item.name);
			values.put(BotContracts.COLUMN_ABOUT, item.desc);
			values.put(BotContracts.COLUMN_API_ENDPOINT, item.endpoint);
			values.put(BotContracts.COLUMN_SECRET, item.secret);
			values.put(BotContracts.COLUMN_GLOBAL_ID, item.Gid);
			values.put(BotContracts.COLUMN_PIC_URI, pic);
			values.put(BotContracts.COLUMN_TIMESTAMP, item.timestamp);
			values.put(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP, item.image_last_update_timestamp);
			return context.getContentResolver().insert(BotContentProvider.CONTENT_URI, values);
		}
		return null;
	}

	public static int UpdateBot(Context context, LocalBotDataModel item) {
		ContentValues values = new ContentValues();
		values.put(BotContracts.COLUMN_ID, item.id);
		values.put(BotContracts.COLUMN_BOT_NAME, item.name);
		values.put(BotContracts.COLUMN_ABOUT, item.desc);
		values.put(BotContracts.COLUMN_API_ENDPOINT, item.endpoint);
		values.put(BotContracts.COLUMN_SECRET, item.secret);
		values.put(BotContracts.COLUMN_GLOBAL_ID, item.Gid);
		values.put(BotContracts.COLUMN_TIMESTAMP, item.timestamp);
		values.put(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP, item.image_last_update_timestamp);
		return context.getContentResolver().update(BotContentProvider.CONTENT_URI, values, BotContracts.COLUMN_ID + " = ?", new String[]{item.id});
	}

	public static int UpdateBot(Context context, LocalBotDataModel item, String img) {
		ContentValues values = new ContentValues();
		values.put(BotContracts.COLUMN_ID, item.id);
		values.put(BotContracts.COLUMN_BOT_NAME, item.name);
		values.put(BotContracts.COLUMN_ABOUT, item.desc);
		values.put(BotContracts.COLUMN_API_ENDPOINT, item.endpoint);
		values.put(BotContracts.COLUMN_SECRET, item.secret);
		values.put(BotContracts.COLUMN_GLOBAL_ID, item.Gid);
		values.put(BotContracts.COLUMN_TIMESTAMP, item.timestamp);
		values.put(BotContracts.COLUMN_PIC_URI, img);
		values.put(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP, item.image_last_update_timestamp);
		return context.getContentResolver().update(BotContentProvider.CONTENT_URI, values, BotContracts.COLUMN_ID + " = ?", new String[]{item.id});
	}

	private static boolean botExists(Context context, String Gid) {
		Cursor cursor = context.getContentResolver().query(BotContentProvider.CONTENT_URI, new String[]{BotContracts._ID}
				, BotContracts.COLUMN_GLOBAL_ID + " = ? ", new String[]{Gid}, null);
		boolean result = cursor != null && cursor.getCount() > 0;
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return result;
	}

}
