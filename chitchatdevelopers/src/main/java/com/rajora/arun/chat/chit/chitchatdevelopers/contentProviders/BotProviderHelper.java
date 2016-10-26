package com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;

/**
 * Created by arc on 19/10/16.
 */

public class BotProviderHelper {
    public static Uri AddBot(Context context,String id,String g_id,String name,String about,String url,String secret,byte[] pic,String pic_url,long timestamp,long img_timestamp,boolean updated){
        ContentValues values = new ContentValues();
        values.put(BotContracts.COLUMN_ID,id);
        values.put(BotContracts.COLUMN_BOT_NAME,name);
        values.put(BotContracts.COLUMN_ABOUT,about);
        values.put(BotContracts.COLUMN_API_ENDPOINT,url);
        values.put(BotContracts.COLUMN_SECRET,secret);
        values.put(BotContracts.COLUMN_PIC,pic);
        values.put(BotContracts.COLUMN_GLOBAL_ID,g_id);
        values.put(BotContracts.COLUMN_PIC_URL,pic_url);
        values.put(BotContracts.COLUMN_TIMESTAMP,timestamp);
        values.put(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP,img_timestamp);
        values.put(BotContracts.COLUMN_UPLOADED,updated);

        return context.getContentResolver().insert(BotContentProvider.CONTENT_URI, values);
    }
    public static Uri AddBot(Context context,String id,String g_id,String name,String about,String url,String secret,byte[] pic,long timestamp,boolean updated){
        ContentValues values = new ContentValues();
        values.put(BotContracts.COLUMN_ID,id);
        values.put(BotContracts.COLUMN_BOT_NAME,name);
        values.put(BotContracts.COLUMN_ABOUT,about);
        values.put(BotContracts.COLUMN_API_ENDPOINT,url);
        values.put(BotContracts.COLUMN_SECRET,secret);
        values.put(BotContracts.COLUMN_PIC,pic);
        values.put(BotContracts.COLUMN_GLOBAL_ID,g_id);
        values.put(BotContracts.COLUMN_TIMESTAMP,timestamp);
        values.put(BotContracts.COLUMN_UPLOADED,updated);

        return context.getContentResolver().insert(BotContentProvider.CONTENT_URI, values);
    }

    public static int UpdateBot(Context context,String id,String g_id,String name,String about,String url,String secret,byte[] pic,String pic_url,long timestamp,long img_timestamp,boolean updated){
        ContentValues values = new ContentValues();
        values.put(BotContracts.COLUMN_ID,id);
        values.put(BotContracts.COLUMN_BOT_NAME,name);
        values.put(BotContracts.COLUMN_ABOUT,about);
        values.put(BotContracts.COLUMN_API_ENDPOINT,url);
        values.put(BotContracts.COLUMN_SECRET,secret);
        values.put(BotContracts.COLUMN_PIC,pic);
        values.put(BotContracts.COLUMN_GLOBAL_ID,g_id);
        values.put(BotContracts.COLUMN_PIC_URL,pic_url);
        values.put(BotContracts.COLUMN_TIMESTAMP,timestamp);
        values.put(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP,img_timestamp);
        values.put(BotContracts.COLUMN_UPLOADED,updated);

        return context.getContentResolver().update(BotContentProvider.CONTENT_URI, values,BotContracts.COLUMN_ID+" = ?", new String[]{id});
    }

    public static int update_Image_Timestamp_Updated(Context context,String id,String pic_url,long img_timestamp,boolean updated){
        ContentValues values = new ContentValues();
        values.put(BotContracts.COLUMN_ID,id);
        values.put(BotContracts.COLUMN_PIC_URL,pic_url);
        values.put(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP,img_timestamp);
        values.put(BotContracts.COLUMN_UPLOADED,updated);

        return context.getContentResolver().update(BotContentProvider.CONTENT_URI, values,BotContracts.COLUMN_ID+" = ?", new String[]{id});
    }

}
