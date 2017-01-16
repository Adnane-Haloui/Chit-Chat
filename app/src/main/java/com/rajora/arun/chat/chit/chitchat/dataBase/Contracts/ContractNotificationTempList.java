package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

/**
 * Created by arc on 4/1/17.
 */

public class ContractNotificationTempList {

	public static final String TABLE_NAME="NOTIFICATION_TEMP_TABLE";

	public static final String COLUMN_CONTACT_ID="CONTACT_ID";
	public static final String COLUMN_IS_BOT="IS_BOT";
	public static final String COLUMN_MESSAGE="MESSAGE";
	public static final String COLUMN_MESSAGE_TYPE="MESSAGE_TYPE";
	public static final String COLUMN_MESSAGE_TIMESTAMP="MESSAGE_TIMESTAMP";

	public static final String TN_COLUMN_CONTACT_ID=TABLE_NAME+"."+COLUMN_CONTACT_ID;
	public static final String TN_COLUMN_IS_BOT=TABLE_NAME+"."+COLUMN_IS_BOT;
	public static final String TN_COLUMN_MESSAGE=TABLE_NAME+"."+COLUMN_MESSAGE;
	public static final String TN_COLUMN_MESSAGE_TYPE=TABLE_NAME+"."+COLUMN_MESSAGE_TYPE;
	public static final String TN_COLUMN_MESSAGE_TIMESTAMP=TABLE_NAME+"."+COLUMN_MESSAGE_TIMESTAMP;
}
