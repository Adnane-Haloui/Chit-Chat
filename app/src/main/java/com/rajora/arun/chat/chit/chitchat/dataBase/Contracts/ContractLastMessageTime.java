package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

import android.provider.BaseColumns;

/**
 * Created by arc on 4/1/17.
 */

public class ContractLastMessageTime implements BaseColumns {

	public static final String TABLE_NAME = "CONTACT_LAST_MESSAGE_TIME_TABLE";
	public static final String COLUMN_CONTACT_ID = "CONTACT_ID";
	public static final String COLUMN_IS_BOT = "IS_BOT";
	public static final String COLUMN_LAST_MESSAGE_TIMESTAMP = "LAST_MESSAGE_TIMESTAMP";

	public static final String TN_COLUMN_CONTACT_ID = TABLE_NAME + "." + COLUMN_CONTACT_ID;
	public static final String TN_COLUMN_IS_BOT = TABLE_NAME + "." + COLUMN_IS_BOT;
	public static final String TN_COLUMN_LAST_MESSAGE_TIMESTAMP = TABLE_NAME + "." + COLUMN_LAST_MESSAGE_TIMESTAMP;
}
