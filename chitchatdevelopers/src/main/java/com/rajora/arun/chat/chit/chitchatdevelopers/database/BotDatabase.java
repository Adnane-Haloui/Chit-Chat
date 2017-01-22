package com.rajora.arun.chat.chit.chitchatdevelopers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class BotDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=5;
    public static final String DATABASE_NAME="bot.db";

    public BotDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BOTS_TABLE="CREATE TABLE "+BotContracts.TABLE_NAME+"("+
                BotContracts.COLUMN_ID+" STRING PRIMARY KEY NOT NULL,"+
                BotContracts.COLUMN_GLOBAL_ID+" STRING NOT NULL,"+
                BotContracts.COLUMN_BOT_NAME+" STRING NOT NULL,"+
                BotContracts.COLUMN_ABOUT+" STRING NOT NULL,"+
                BotContracts.COLUMN_PIC+" BLOB,"+
                BotContracts.COLUMN_PIC_URL+" STRING,"+
                BotContracts.COLUMN_API_ENDPOINT+" STRING NOT NULL,"+
                BotContracts.COLUMN_SECRET+" STRING,"+
                BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP+" INTEGER,"+
                BotContracts.COLUMN_TIMESTAMP+" INTEGER,"+
                BotContracts.COLUMN_UPLOADED+" BOOLEAN"+
                " );";
        db.execSQL(SQL_CREATE_BOTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+BotContracts.TABLE_NAME);
        onCreate(db);
    }
}
