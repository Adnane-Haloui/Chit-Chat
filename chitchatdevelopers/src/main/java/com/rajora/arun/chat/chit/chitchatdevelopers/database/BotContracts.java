package com.rajora.arun.chat.chit.chitchatdevelopers.database;

import android.provider.BaseColumns;

/**
 * Created by arc on 19/10/16.
 */

public class BotContracts implements BaseColumns {
    public static final String TABLE_NAME="BOTS";

    public static final String COLUMN_ID="ID";
    public static final String COLUMN_GLOBAL_ID="GLOBAL_ID";
    public static final String COLUMN_BOT_NAME="NAME";
    public static final String COLUMN_API_ENDPOINT="API_ENDPOINT";
    public static final String COLUMN_SECRET="SECRET";
    public static final String COLUMN_ABOUT="ABOUT";
    public static final String COLUMN_PIC_URL="PIC_URL";
    public static final String COLUMN_PIC="PIC";
    public static final String COLUMN_TIMESTAMP="TIMESTAMP";
    public static final String COLUMN_IMAGE_UPDATE_TIMESTAMP="IMAGE_UPDATE_TIMESTAMP";
    public static final String COLUMN_UPLOADED="UPLOADED";

}
