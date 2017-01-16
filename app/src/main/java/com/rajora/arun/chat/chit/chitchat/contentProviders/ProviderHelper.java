package com.rajora.arun.chat.chit.chitchat.contentProviders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;


public class ProviderHelper {

    public static void updateContact(Context context,String name,String ph_no,boolean is_user,String pic_uri){
        ContentValues values=new ContentValues();
        values.put(ContractContacts.COLUMN_NAME,name);
        values.put(ContractContacts.COLUMN_CONTACT_ID,ph_no);
        values.put(ContractContacts.COLUMN_IS_USER,is_user);
        values.put(ContractContacts.COLUMN_PIC_URI,pic_uri);
        context.getContentResolver().update(
                ChatContentProvider.CONTACT_LIST_URI,
                values,
                ContractContacts.COLUMN_CONTACT_ID+" = ? AND "+ContractContacts.COLUMN_IS_BOT+" = ? ",
                new String[]{ph_no,"0"});
    }

    public static void updateContact(Context context,String about,String ph_no,boolean is_user){
        ContentValues values=new ContentValues();
        values.put(ContractContacts.COLUMN_ABOUT,about);
        values.put(ContractContacts.COLUMN_IS_USER,is_user);
        context.getContentResolver().update(
                ChatContentProvider.CONTACT_LIST_URI,
                values,
                ContractContacts.COLUMN_CONTACT_ID+" = ? AND "+ContractContacts.COLUMN_IS_BOT+" = ? ",
                new String[]{ph_no,"0"});
    }

    public static void addContact(Context context,String name,String ph_no,boolean is_user,String pic){
        ContentValues values=new ContentValues();
        values.put(ContractContacts.COLUMN_NAME,name);
        values.put(ContractContacts.COLUMN_CONTACT_ID,ph_no);
        values.put(ContractContacts.COLUMN_IS_USER,is_user);
        values.put(ContractContacts.COLUMN_PIC_URI,pic);
        context.getContentResolver().insert(ChatContentProvider.CONTACT_LIST_URI,values);
    }

    public static void handleMessageInDatabase(Context context,ChatItemDataModel item){
        context.getContentResolver().insert(ChatContentProvider.CHAT_URI,item.getMessageContentValues());
    }
}
