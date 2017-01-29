package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

import android.provider.BaseColumns;

/**
 * Created by arc on 4/1/17.
 */

public class ContractUnreadCount implements BaseColumns {
	public static final String TABLE_NAME = "CONTACT_UNREAD_COUNT";

	public static final String COLUMN_UNREAD_COUNT = "UNREAD_COUNT";

	public static final String TN_COLUMN_UNREAD_COUNT = TABLE_NAME + "." + COLUMN_UNREAD_COUNT;
	public static final String TN_COLUMN_ID = TABLE_NAME + "." + _ID;

}
