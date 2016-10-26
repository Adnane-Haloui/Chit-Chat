package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

import android.provider.BaseColumns;

/**
 * Created by arc on 19/10/16.
 */

public class contract_bots implements BaseColumns {
    public static final String TABLE_NAME="BOTS";

    public static final String COLUMN_PH_NUMBER="PHONE_NUMBER";
    public static final String COLUMN_NAME="NAME";
    public static final String COLUMN_ABOUT="ABOUT";
    public static final String COLUMN_PIC="PIC";
    public static final String COLUMN_BOT_ID="ID";
    public static final String COLUMN_PIC_URL="PIC_URL";
    public static final String COLUMN_PIC_TIMESTAMP="PIC_TIMESTAMP";
    public static final String COLUMN_DEVELOPER_NAME="DEVELOPER_NAME";
    public static final String COLUMN_TIMESTAMP="TIMESTAMP";
}
