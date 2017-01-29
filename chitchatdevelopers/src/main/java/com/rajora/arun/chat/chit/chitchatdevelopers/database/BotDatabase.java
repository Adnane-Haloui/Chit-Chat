package com.rajora.arun.chat.chit.chitchatdevelopers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BotDatabase extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 8;
	private static final String DATABASE_NAME = "bot.db";

	public BotDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		final String SQL_CREATE_BOTS_TABLE = "CREATE TABLE " + BotContracts.TABLE_NAME + "(" +
				BotContracts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
				BotContracts.COLUMN_ID + " STRING NOT NULL UNIQUE ," +
				BotContracts.COLUMN_GLOBAL_ID + " STRING NOT NULL ," +
				BotContracts.COLUMN_BOT_NAME + " STRING NOT NULL ," +
				BotContracts.COLUMN_ABOUT + " STRING NOT NULL ," +
				BotContracts.COLUMN_PIC_URI + " STRING ," +
				BotContracts.COLUMN_API_ENDPOINT + " STRING NOT NULL ," +
				BotContracts.COLUMN_SECRET + " STRING ," +
				BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP + " INTEGER ," +
				BotContracts.COLUMN_TIMESTAMP + " INTEGER " + " );";
		db.execSQL(SQL_CREATE_BOTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + BotContracts.TABLE_NAME);
		onCreate(db);
	}
}
