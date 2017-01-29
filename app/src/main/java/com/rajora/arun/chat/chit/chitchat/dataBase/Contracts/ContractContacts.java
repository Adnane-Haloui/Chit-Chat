package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

import android.provider.BaseColumns;

/**
 * Created by arc on 3/1/17.
 */

public class ContractContacts implements BaseColumns {

	public static final String TABLE_NAME = "CONTACTS_TABLE";

	public static final String COLUMN_CONTACT_ID = "CONTACT_ID";
	public static final String COLUMN_IS_BOT = "IS_BOT";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_ABOUT = "ABOUT";
	public static final String COLUMN_PIC_URL = "PIC_URL";
	public static final String COLUMN_IS_USER = "IS_USER";
	public static final String COLUMN_DEV_NAME = "DEV_NAME";
	public static final String COLUMN_PIC_URI = "PIC_URI";

	public static final String TN_COLUMN_CONTACT_ID = TABLE_NAME + "." + COLUMN_CONTACT_ID;
	public static final String TN_COLUMN_IS_BOT = TABLE_NAME + "." + COLUMN_IS_BOT;
	public static final String TN_COLUMN_NAME = TABLE_NAME + "." + COLUMN_NAME;
	public static final String TN_COLUMN_ABOUT = TABLE_NAME + "." + COLUMN_ABOUT;
	public static final String TN_COLUMN_PIC_URL = TABLE_NAME + "." + COLUMN_PIC_URL;
	public static final String TN_COLUMN_IS_USER = TABLE_NAME + "." + COLUMN_IS_USER;
	public static final String TN_COLUMN_DEV_NAME = TABLE_NAME + "." + COLUMN_DEV_NAME;
	public static final String TN_COLUMN_PIC_URI = TABLE_NAME + "." + COLUMN_PIC_URI;
	public static final String TN_COLUMN_ID = TABLE_NAME + "." + _ID;

}
