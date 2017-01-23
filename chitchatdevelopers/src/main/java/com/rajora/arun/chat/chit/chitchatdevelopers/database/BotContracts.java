package com.rajora.arun.chat.chit.chitchatdevelopers.database;

import android.provider.BaseColumns;


public class BotContracts implements BaseColumns {
    public static final String TABLE_NAME="BOTS";

    public static final String COLUMN_ID="ID";
    public static final String COLUMN_GLOBAL_ID="GLOBAL_ID";
    public static final String COLUMN_BOT_NAME="NAME";
    public static final String COLUMN_API_ENDPOINT="API_ENDPOINT";
    public static final String COLUMN_SECRET="SECRET";
    public static final String COLUMN_ABOUT="ABOUT";
    public static final String COLUMN_PIC_URI="PIC_URI";
    public static final String COLUMN_TIMESTAMP="TIMESTAMP";
    public static final String COLUMN_IMAGE_UPDATE_TIMESTAMP="IMAGE_UPDATE_TIMESTAMP";
}
