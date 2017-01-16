package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

import android.provider.BaseColumns;

/**
 * Created by arc on 3/1/17.
 */

public class ContractChat implements BaseColumns {

	public static final String TABLE_NAME="CHAT_TABLE";

	public static final String COLUMN_CHAT_ID="CHAT_ID";
	public static final String COLUMN_CONTACT_ID="CONTACT_ID";
	public static final String COLUMN_IS_BOT="IS_BOT";
	public static final String COLUMN_MESSAGE_DIRECTION="MESSAGE_DIRECTION";
	public static final String COLUMN_TIMESTAMP="TIMESTAMP";
	public static final String COLUMN_MESSAGE="MESSAGE";
	public static final String COLUMN_MESSAGE_TYPE="MESSAGE_TYPE";
	public static final String COLUMN_MESSAGE_STATUS="MESSAGE_STATUS";
	public static final String COLUMN_UPLOAD_STATUS="UPLOAD_STATUS";
	public static final String COLUMN_EXTRA_URI="EXTRA_URI";

	public static final String TN_COLUMN_CHAT_ID=TABLE_NAME+"."+COLUMN_CHAT_ID;
	public static final String TN_COLUMN_CONTACT_ID=TABLE_NAME+"."+COLUMN_CONTACT_ID;
	public static final String TN_COLUMN_IS_BOT=TABLE_NAME+"."+COLUMN_IS_BOT;
	public static final String TN_COLUMN_MESSAGE_DIRECTION=TABLE_NAME+"."+COLUMN_MESSAGE_DIRECTION;
	public static final String TN_COLUMN_TIMESTAMP=TABLE_NAME+"."+COLUMN_TIMESTAMP;
	public static final String TN_COLUMN_MESSAGE=TABLE_NAME+"."+COLUMN_MESSAGE;
	public static final String TN_COLUMN_MESSAGE_TYPE=TABLE_NAME+"."+COLUMN_MESSAGE_TYPE;
	public static final String TN_COLUMN_MESSAGE_STATUS=TABLE_NAME+"."+COLUMN_MESSAGE_STATUS;
	public static final String TN_COLUMN_UPLOAD_STATUS=TABLE_NAME+"."+COLUMN_UPLOAD_STATUS;
	public static final String TN_COLUMN_EXTRA_URI=TABLE_NAME+"."+COLUMN_EXTRA_URI;
	public static final String TN_COLUMN_ID=TABLE_NAME+"."+_ID;

}
