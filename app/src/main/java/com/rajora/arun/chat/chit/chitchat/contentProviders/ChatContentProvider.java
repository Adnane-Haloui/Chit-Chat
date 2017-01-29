package com.rajora.arun.chat.chit.chitchat.contentProviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChatList;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractNotificationList;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractUnreadCount;
import com.rajora.arun.chat.chit.chitchat.dataBase.Helper.ChatDatabase;

public class ChatContentProvider extends ContentProvider {

	public static final String BROADCAST_STRING = "com.rajora.arun.chat.chit.chitchat.app.ACTION_DATA_UPDATED";

	public static final String PROVIDER_NAME = "com.rajora.arun.chit.chat.provider";

	public static final String CONTACT_LIST_URL = "content://" + PROVIDER_NAME + "/contact_list";
	public static final String CHAT_LIST_URL = "content://" + PROVIDER_NAME + "/chat_list";
	public static final String CHAT_URL = "content://" + PROVIDER_NAME + "/chat";
	public static final String NOTIFICATION_LIST_URL = "content://" + PROVIDER_NAME + "/notification_list";
	public static final String UNREAD_COUNT_URL = "content://" + PROVIDER_NAME + "/unread_count";

	public static final Uri CONTACT_LIST_URI = Uri.parse(CONTACT_LIST_URL);
	public static final Uri NOTIFICATION_LIST_URI = Uri.parse(NOTIFICATION_LIST_URL);
	public static final Uri UNREAD_COUNT_URI = Uri.parse(UNREAD_COUNT_URL);
	public static final Uri CHATS_LIST_URI = Uri.parse(CHAT_LIST_URL);
	public static final Uri CHAT_URI = Uri.parse(CHAT_URL);

	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sUriMatcher.addURI(PROVIDER_NAME, "contact_list", 1);
		sUriMatcher.addURI(PROVIDER_NAME, "contact_list/*", 2);

		sUriMatcher.addURI(PROVIDER_NAME, "chat_list", 3);
		sUriMatcher.addURI(PROVIDER_NAME, "chat_list/*", 4);

		sUriMatcher.addURI(PROVIDER_NAME, "notification_list", 5);

		sUriMatcher.addURI(PROVIDER_NAME, "chat", 6);
		sUriMatcher.addURI(PROVIDER_NAME, "chat/*", 7);

		sUriMatcher.addURI(PROVIDER_NAME, "unread_count", 8);
	}

	private ChatDatabase mBotDatabase;
	private SQLiteDatabase db;

	public ChatContentProvider() {
	}

	private int deleteValues(String table_name, String selection, String[] selectionArgs) {
		return db.delete(table_name, selection, selectionArgs);
	}

	private int deleteItems(Uri uri, String table_name, String column_id, String selection, String[] selectionArgs) {
		String id = uri.getPathSegments().get(1);
		return db.delete(table_name, column_id + " = " + id +
				(TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')'), selectionArgs);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		db = mBotDatabase.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
			case 1:
				count = deleteValues(ContractContacts.TABLE_NAME, selection, selectionArgs);
				break;
			case 2:
				count = deleteItems(uri, ContractContacts.TABLE_NAME, ContractContacts._ID, selection, selectionArgs);
				break;
			case 3:
			case 4:
				throw new UnsupportedOperationException("Cannot delete values from notification list view");
			case 5:
				throw new UnsupportedOperationException("Cannot delete values from chat list view");
			case 6:
				count = deleteValues(ContractChat.TABLE_NAME, selection, selectionArgs);
				break;
			case 7:
				count = deleteItems(uri, ContractChat.TABLE_NAME, ContractChat._ID, selection, selectionArgs);
				break;
			case 8:
				throw new UnsupportedOperationException("Cannot delete values from unread_message_count table");
			default:
				throw new UnsupportedOperationException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		getContext().getContentResolver().notifyChange(NOTIFICATION_LIST_URI, null);
		getContext().getContentResolver().notifyChange(CHATS_LIST_URI, null);
		SendBroadcastChatListDataChanged();
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case 1:
				return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.contact_list";
			case 2:
				return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chat.provider.contact_list";
			case 3:
				return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.chat_list";
			case 4:
				return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chat.provider.chat_list";
			case 5:
				return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.notification_list";
			case 6:
				return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.chat";
			case 7:
				return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chat.provider.chat";
			case 8:
				return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.unread_count";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		db = mBotDatabase.getWritableDatabase();
		long rowId = 0;
		Uri baseUri = null;
		switch (sUriMatcher.match(uri)) {
			case 1:
			case 2:
				baseUri = CONTACT_LIST_URI;
				rowId = db.insertWithOnConflict(ContractContacts.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
				break;
			case 3:
			case 4:
				throw new IllegalArgumentException("Cannot insert values into chat list");
			case 5:
				throw new IllegalArgumentException("Cannot insert values into notification list");
			case 6:
			case 7:
				baseUri = CHAT_URI;
				rowId = db.insertWithOnConflict(ContractChat.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_IGNORE);
				break;
			case 8:
				throw new UnsupportedOperationException("Cannot insert values into unread_message_count table");
		}
		if (rowId > 0) {
			Uri _uri = ContentUris.withAppendedId(baseUri, rowId);
			Uri _notification_uri = ContentUris.withAppendedId(NOTIFICATION_LIST_URI, rowId);
			Uri _chat_list_uri = ContentUris.withAppendedId(CHATS_LIST_URI, rowId);

			getContext().getContentResolver().notifyChange(_uri, null);
			getContext().getContentResolver().notifyChange(_notification_uri, null);
			getContext().getContentResolver().notifyChange(_chat_list_uri, null);

			SendBroadcastChatListDataChanged();

			return _uri;
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		mBotDatabase = new ChatDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
	                    String[] selectionArgs, String sortOrder) {
		db = mBotDatabase.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String primary_column = "", sortColumn = "";
		switch (sUriMatcher.match(uri)) {
			case 1:
			case 2:
				primary_column = ContractContacts._ID;
				sortColumn = ContractContacts.COLUMN_NAME;
				queryBuilder.setTables(ContractContacts.TABLE_NAME);
				break;
			case 3:
			case 4:
				primary_column = ContractChatList._ID;
				sortColumn = ContractChatList.COLUMN_NAME;
				queryBuilder.setTables(ContractChatList.TABLE_NAME);
				break;
			case 5:
				primary_column = ContractNotificationList.COLUMN_CONTACT_ID;
				sortColumn = ContractNotificationList.COLUMN_MESSAGE_TIMESTAMP;
				queryBuilder.setTables(ContractNotificationList.TABLE_NAME);
				break;
			case 6:
			case 7:
				primary_column = ContractChat._ID;
				sortColumn = ContractChat.COLUMN_TIMESTAMP + " DESC ";
				queryBuilder.setTables(ContractChat.TABLE_NAME);
				break;
			case 8:
				primary_column = ContractUnreadCount._ID;
				sortColumn = ContractUnreadCount.COLUMN_UNREAD_COUNT;
				queryBuilder.setTables(ContractUnreadCount.TABLE_NAME);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		switch (sUriMatcher.match(uri)) {
			case 1:
			case 3:
			case 5:
			case 6:
			case 8:
				break;
			case 2:
			case 4:
			case 7:
				queryBuilder.appendWhere(primary_column + "=" + uri.getPathSegments().get(1));
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		if (sortOrder == null || sortOrder.isEmpty()) {
			sortOrder = sortColumn;
		}
		Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	private int updateValues(String table_name, ContentValues contentValues, String selection, String[] selectionArgs) {
		return db.update(table_name, contentValues, selection, selectionArgs);
	}

	private int updateItems(Uri uri, String table_name, ContentValues contentValues, String column_id, String selection, String[] selectionArgs) {
		String id = uri.getPathSegments().get(1);
		return db.update(table_name, contentValues, column_id + " = " + id +
				(TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')'), selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
	                  String[] selectionArgs) {
		int count = 0;
		db = mBotDatabase.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
			case 1:
				count = updateValues(ContractContacts.TABLE_NAME, values, selection, selectionArgs);
				break;
			case 2:
				count = updateItems(uri, ContractChat.TABLE_NAME, values, ContractContacts._ID, selection, selectionArgs);
				break;
			case 3:
			case 4:
				throw new UnsupportedOperationException("cannot update values in chat list view");
			case 5:
				throw new UnsupportedOperationException("cannot update values in notification list view");
			case 6:
				count = updateValues(ContractChat.TABLE_NAME, values, selection, selectionArgs);
				break;
			case 7:
				count = updateItems(uri, ContractChat.TABLE_NAME, values, ContractChat._ID, selection, selectionArgs);
				break;
			case 8:
				throw new UnsupportedOperationException("Cannot update values of unread_message_count table");
			default:
				throw new UnsupportedOperationException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		getContext().getContentResolver().notifyChange(NOTIFICATION_LIST_URI, null);
		getContext().getContentResolver().notifyChange(CHATS_LIST_URI, null);
		SendBroadcastChatListDataChanged();
		return count;
	}

	private void SendBroadcastChatListDataChanged() {
		Intent intent = new Intent(BROADCAST_STRING);
		getContext().sendBroadcast(intent);
	}
}