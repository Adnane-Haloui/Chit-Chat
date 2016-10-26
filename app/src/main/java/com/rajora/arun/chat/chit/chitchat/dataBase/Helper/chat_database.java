package com.rajora.arun.chat.chit.chitchat.dataBase.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_bots;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chats;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

/**
 * Created by arc on 19/10/16.
 */

public class chat_database extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=5;
    public static final String DATABASE_NAME="chat.db";

    public chat_database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CHAT_TABLE="CREATE TABLE "+ contract_chat.TABLE_NAME+"("+
                 contract_chat.COLUMN_ID+" INTEGER AUTO INCREMENT PRIMARY KEY,"+
                 contract_chat.COLUMN_MESSAGE_ID_ON_SERVER+" STRING,"+
                 contract_chat.COLUMN_MESSAGE_SENDER_NUMBER+" STRING,"+
                contract_chat.COLUMN_MESSAGE_SENDER_ID+" STRING,"+
                 contract_chat.COLUMN_IS_BOT+" BOOLEAN NOT NULL,"+
                 contract_chat.COLUMN_MESSAGE_TYPE+" STRING NOT NULL,"+
                 contract_chat.COLUMN_MESSAGE+" STRING NOT NULL,"+
                 contract_chat.COLUMN_TIMESTAMP+" INTEGER"+
                contract_chat.COLUMN_MESSAGE_STATUS+" STRING"+
                " );";

        final String SQL_CREATE_CHATS_TABLE="CREATE TABLE "+ contract_chats.TABLE_NAME+"("+
                contract_chats.COLUMN_ID+" STRING PRIMARY KEY,"+
                contract_chats.COLUMN_PH_NUMBER+" TEXT ,"+
                contract_chats.COLUMN_BOT_ID+" STRING ,"+
                contract_chats.COLUMN_IS_BOT+" BOOLEAN NOT NULL,"+
                contract_chats.COLUMN_NAME+" STRING,"+
                contract_chats.COLUMN_ABOUT+" STRING,"+
                contract_chats.COLUMN_PIC+" BLOB,"+
                contract_chats.COLUMN_LAST_MESSAGE+" STRING,"+
                contract_chats.COLUMN_LAST_MESSAGE_TIME+" STRING"+
                " );";

        final String SQL_CREATE_CONTACTS_TABLE="CREATE TABLE "+ contract_contacts.TABLE_NAME+"("+
                contract_contacts.COLUMN_PH_NUMBER+" TEXT PRIMARY KEY,"+
                contract_contacts.COLUMN_NAME+" STRING,"+
                contract_contacts.COLUMN_ABOUT+" STRING,"+
                contract_contacts.COLUMN_PIC+" BLOB,"+
                contract_contacts.COLUMN_PIC_TIMESTAMP+" INTEGER,"+
                contract_contacts.COLUMN_PIC_URL+" STRING,"+
                contract_contacts.COLUMN_IS_USER+" BOOLEAN NOT NULL DEFAULT 0"+
                " );";

        final String SQL_CREATE_BOTS_TABLE="CREATE TABLE "+contract_bots.TABLE_NAME+"("+
                contract_bots.COLUMN_PH_NUMBER+" STRING NOT NULL,"+
                contract_bots.COLUMN_BOT_ID+" STRING PRIMARY KEY,"+
                contract_bots.COLUMN_NAME+" STRING NOT NULL,"+
                contract_bots.COLUMN_ABOUT+" STRING ,"+
                contract_bots.COLUMN_PIC_URL+" STRING ,"+
                contract_bots.COLUMN_PIC_TIMESTAMP+" INTEGER ,"+
                contract_bots.COLUMN_DEVELOPER_NAME+" STRING ,"+
                contract_bots.COLUMN_TIMESTAMP+" STRING ,"+
                contract_bots.COLUMN_PIC+" BLOB"+
                " );";

        Log.d("findme",SQL_CREATE_BOTS_TABLE);
        Log.d("findme",SQL_CREATE_CHAT_TABLE);
        Log.d("findme",SQL_CREATE_CHATS_TABLE);
        Log.d("findme",SQL_CREATE_CONTACTS_TABLE);

        db.execSQL(SQL_CREATE_CHAT_TABLE);
        db.execSQL(SQL_CREATE_BOTS_TABLE);
        db.execSQL(SQL_CREATE_CHATS_TABLE);
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+contract_bots.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+contract_chats.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+contract_chat.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+contract_contacts.TABLE_NAME);

        onCreate(db);
    }
}
