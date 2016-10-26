package com.rajora.arun.chat.chit.chitchat.dataBase.Contracts;

import android.provider.BaseColumns;

/**
 * Created by arc on 19/10/16.
 */

public class contract_chat implements BaseColumns {
    public static final String TABLE_NAME="CHAT";

    public static final String COLUMN_ID="ID";
    public static final String COLUMN_MESSAGE_ID_ON_SERVER="SERVER_ID";
    public static final String COLUMN_MESSAGE_SENDER_NUMBER="PHONE_NUMBER";
    public static final String COLUMN_MESSAGE_SENDER_ID="SENDER_ID";
    public static final String COLUMN_MESSAGE_RECEIVER_ID="RECEIVER_ID";
    public static final String COLUMN_IS_BOT="IS_BOT";
    public static final String COLUMN_TIMESTAMP="TIMESTAMP";
    public static final String COLUMN_MESSAGE="MESSAGE";
    public static final String COLUMN_MESSAGE_TYPE="MESSAGE_TYPE";
    public static final String COLUMN_MESSAGE_STATUS="CHAT_STATUS";

}
