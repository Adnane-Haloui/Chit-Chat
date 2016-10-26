package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

import android.provider.BaseColumns;

/**
 * Created by arc on 19/10/16.
 */

public class contract_chats implements BaseColumns{
    public static final String TABLE_NAME="CHATS";

    public static final String COLUMN_ID="ID";
    public static final String COLUMN_PH_NUMBER="PHONE_NUMBER";
    public static final String COLUMN_NAME="NAME";
    public static final String COLUMN_BOT_ID="BOT_ID";
    public static final String COLUMN_ABOUT="ABOUT";
    public static final String COLUMN_PIC="PIC";
    public static final String COLUMN_LAST_MESSAGE="LAST_MESSAGE";
    public static final String COLUMN_LAST_MESSAGE_TIME="TIME";
    public static final String COLUMN_IS_BOT="IS_BOT";
    public static final String COLUMN_BOT_DEV_NAME="BOT_DEV_NAME";
    public static final String COLUMN_BOT_PIC_URL="BOT_PIC_URL";

}
