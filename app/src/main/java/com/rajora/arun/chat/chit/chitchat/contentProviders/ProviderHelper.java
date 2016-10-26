package com.rajora.arun.chat.chit.chitchat.contentProviders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_bots;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

/**
 * Created by arc on 21/10/16.
 */

public class ProviderHelper {
    public static Uri AddBot(Context context, String id, String name,String dev_name,String ph_number, String about, String pic_url, long timestamp, long pic_timestamp){
        ContentValues values = new ContentValues();
        values.put(contract_bots.COLUMN_ABOUT,about);
        values.put(contract_bots.COLUMN_BOT_ID,id);
        values.put(contract_bots.COLUMN_DEVELOPER_NAME,dev_name);
        values.put(contract_bots.COLUMN_NAME,name);
        values.put(contract_bots.COLUMN_PH_NUMBER,ph_number);
        values.put(contract_bots.COLUMN_PIC_URL,pic_url);
        values.put(contract_bots.COLUMN_PIC_TIMESTAMP,pic_timestamp);
        values.put(contract_bots.COLUMN_TIMESTAMP,timestamp);

        return context.getContentResolver().insert(ChatContentProvider.BOTS_URI, values);
    }
    public static Cursor getContact(Context context, String ph_number){
        return context.getContentResolver().query(ChatContentProvider.CONTACTS_URI,new String[]{
                contract_contacts.COLUMN_PIC_TIMESTAMP,
                contract_contacts.COLUMN_PIC,
                contract_contacts.COLUMN_ABOUT,
                contract_contacts.COLUMN_PIC_URL,
        }, contract_contacts.COLUMN_PH_NUMBER+" = ?",new String[]{ph_number},null);
    }
}
