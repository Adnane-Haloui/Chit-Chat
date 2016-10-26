package com.rajora.arun.chat.chit.chitchat.contentProviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.Selection;
import android.text.TextUtils;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_bots;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chats;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;
import com.rajora.arun.chat.chit.chitchat.dataBase.Helper.chat_database;
import java.util.HashMap;

public class ChatContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.rajora.arun.chit.chat.provider";

    public static final String CONTACTS_URL = "content://" + PROVIDER_NAME + "/contacts";
    public static final String BOTS_URL = "content://" + PROVIDER_NAME + "/bots";
    public static final String CHATS_URL = "content://" + PROVIDER_NAME + "/chats";
    public static final String CHAT_URL = "content://" + PROVIDER_NAME + "/chat";

    public static final Uri CONTACTS_URI = Uri.parse(CONTACTS_URL);
    public static final Uri BOTS_URI = Uri.parse(BOTS_URL);
    public static final Uri CHATS_URI = Uri.parse(CHATS_URL);
    public static final Uri CHAT_URI = Uri.parse(CHAT_URL);

    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;
    private chat_database mBotDatabase;
    private SQLiteDatabase db;

    static {
        sUriMatcher.addURI(PROVIDER_NAME,"contacts",1);
        sUriMatcher.addURI(PROVIDER_NAME,"contacts/*",2);

        sUriMatcher.addURI(PROVIDER_NAME,"bots",3);
        sUriMatcher.addURI(PROVIDER_NAME,"bots/*",4);

        sUriMatcher.addURI(PROVIDER_NAME,"chats",5);
        sUriMatcher.addURI(PROVIDER_NAME,"chats/*",6);

        sUriMatcher.addURI(PROVIDER_NAME,"chat",7);
        sUriMatcher.addURI(PROVIDER_NAME,"chat/*",8);
    }

    public ChatContentProvider() {
    }

    private int deleteValues(String table_name, String selection,String[] selectionArgs){
        return db.delete(table_name, selection, selectionArgs);
    }
    private int deleteItems(Uri uri,String table_name,String column_id, String selection,String[] selectionArgs){
        String id = uri.getPathSegments().get(1);
        return db.delete(table_name, column_id +  " = " + id +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        db=mBotDatabase.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case 1:
                count=deleteValues(contract_contacts.TABLE_NAME,selection,selectionArgs);
                break;
            case 2:
                count = deleteItems(uri, contract_contacts.TABLE_NAME, contract_contacts.COLUMN_PH_NUMBER,selection , selectionArgs);
                break;
            case 3:
                count=deleteValues(contract_bots.TABLE_NAME,selection,selectionArgs);
                break;
            case 4:
                count = deleteItems(uri, contract_bots.TABLE_NAME, contract_bots.COLUMN_BOT_ID,selection , selectionArgs);
                break;
            case 5:
                count=deleteValues(contract_chats.TABLE_NAME,selection,selectionArgs);
                break;
            case 6:
                count = deleteItems(uri, contract_chats.TABLE_NAME, contract_chats.COLUMN_ID,selection , selectionArgs);
                break;
            case 7:
                count=deleteValues(contract_chat.TABLE_NAME,selection,selectionArgs);
                break;
            case 8:
                count = deleteItems(uri, contract_chat.TABLE_NAME, contract_chat.COLUMN_ID,selection , selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case 1:
                return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.contacts";
            case 2:
                return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chat.provider.contacts";
            case 3:
                return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.bots";
            case 4:
                return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chat.provider.bots";
            case 5:
                return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.chats";
            case 6:
                return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chat.provider.chats";
            case 7:
                return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chat.provider.chat";
            case 8:
                return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chat.provider.chat";
        }
       return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        db=mBotDatabase.getWritableDatabase();
        long rowId=0;
        Uri baseUri=null;
        switch (sUriMatcher.match(uri)){
            case 1:
            case 2:
                baseUri=CONTACTS_URI;
                rowId=db.insert(contract_contacts.TABLE_NAME,"",values);
                break;
            case 3:
            case 4:
                baseUri=BOTS_URI;
                rowId=db.insert(contract_bots.TABLE_NAME,"",values);
                break;
            case 5:
            case 6:
                baseUri=CHATS_URI;
                rowId=db.insert(contract_chats.TABLE_NAME,"",values);
                break;
            case 7:
            case 8:
                baseUri=CHAT_URI;
                rowId=db.insert(contract_chat.TABLE_NAME,"",values);
                break;
        }
        if (rowId > 0)
        {
            Uri _uri = ContentUris.withAppendedId(baseUri,rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add item(s) into " + uri);
    }

    @Override
    public boolean onCreate() {
        mBotDatabase=new chat_database(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        db=mBotDatabase.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        String primary_column="",sortColumn="";
        switch (sUriMatcher.match(uri)){
            case 1:
                primary_column=contract_contacts.COLUMN_PH_NUMBER;
                sortColumn=contract_contacts.COLUMN_NAME;
                queryBuilder.setTables(contract_contacts.TABLE_NAME);
                break;
            case 2:
                primary_column=contract_contacts.COLUMN_PH_NUMBER;
                sortColumn=contract_contacts.COLUMN_NAME;
                queryBuilder.setTables(contract_contacts.TABLE_NAME);
                break;
            case 3:
                primary_column=contract_bots.COLUMN_BOT_ID;
                sortColumn=contract_contacts.COLUMN_NAME;
                queryBuilder.setTables(contract_bots.TABLE_NAME);
                break;
            case 4:
                primary_column=contract_bots.COLUMN_BOT_ID;
                sortColumn=contract_contacts.COLUMN_NAME;
                queryBuilder.setTables(contract_bots.TABLE_NAME);
                break;
            case 5:
                primary_column=contract_chats.COLUMN_ID;
                sortColumn=contract_chats.COLUMN_LAST_MESSAGE_TIME;
                queryBuilder.setTables(contract_chats.TABLE_NAME);
                break;
            case 6:
                primary_column=contract_chats.COLUMN_ID;
                sortColumn=contract_chats.COLUMN_LAST_MESSAGE_TIME;
                queryBuilder.setTables(contract_chats.TABLE_NAME);
                break;
            case 7:
                primary_column=contract_chat.COLUMN_ID;
                sortColumn=contract_chat.COLUMN_ID;
                queryBuilder.setTables(contract_chat.TABLE_NAME);
                break;
            case 8:
                primary_column=contract_chat.COLUMN_ID;
                sortColumn=contract_chat.COLUMN_ID;
                queryBuilder.setTables(contract_chat.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        switch (sUriMatcher.match(uri)){
            case 1:
            case 3:
            case 5:
            case 7:
                queryBuilder.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;
            case 2:
            case 4:
            case 6:
            case 8:
                queryBuilder.appendWhere( primary_column+ "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
           sortOrder=sortColumn;
        }
        Cursor c = queryBuilder.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }
    private int updateValues(String table_name,ContentValues contentValues, String selection,String[] selectionArgs){
        return db.update(table_name,contentValues, selection, selectionArgs);
    }
    private int updateItems(Uri uri,String table_name,ContentValues contentValues,String column_id, String selection,String[] selectionArgs){
        String id = uri.getPathSegments().get(1);
        return db.update(table_name,contentValues, column_id +  " = " + id +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        db=mBotDatabase.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case 1:
                count=updateValues(contract_contacts.TABLE_NAME,values,selection,selectionArgs);
                break;
            case 2:
                count = updateItems(uri, contract_contacts.TABLE_NAME,values, contract_contacts.COLUMN_PH_NUMBER,selection , selectionArgs);
                break;
            case 3:
                count=updateValues(contract_bots.TABLE_NAME,values,selection,selectionArgs);
                break;
            case 4:
                count = updateItems(uri, contract_bots.TABLE_NAME,values, contract_bots.COLUMN_BOT_ID,selection , selectionArgs);
                break;
            case 5:
                count=updateValues(contract_chats.TABLE_NAME,values,selection,selectionArgs);
                break;
            case 6:
                count = updateItems(uri, contract_chats.TABLE_NAME, values,contract_chats.COLUMN_ID,selection , selectionArgs);
                break;
            case 7:
                count=updateValues(contract_chat.TABLE_NAME,values,selection,selectionArgs);
                break;
            case 8:
                count = updateItems(uri, contract_chat.TABLE_NAME, values,contract_chat.COLUMN_ID,selection , selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
