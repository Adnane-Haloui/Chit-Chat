package com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.QuickContactBadge;

import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotDatabase;

import java.util.HashMap;

public class BotContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.rajora.arun.chit.chatdevelopers.bots.provider";
    public static final String URL = "content://" + PROVIDER_NAME + "/bot";
    public static final Uri CONTENT_URI = Uri.parse(URL);
    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;
    private BotDatabase mBotDatabase;
    private SQLiteDatabase db;

    static {
        sUriMatcher.addURI(PROVIDER_NAME,"bot",1);
        sUriMatcher.addURI(PROVIDER_NAME,"bot/*",2);
    }

    public BotContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        db=mBotDatabase.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case 1:
                count = db.delete(BotContracts.TABLE_NAME, selection, selectionArgs);
                break;

            case 2:
                String id = uri.getPathSegments().get(1);
                count = db.delete( BotContracts.TABLE_NAME, BotContracts.COLUMN_ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
       if(sUriMatcher.match(uri)==1){
           return "vnd.android.cursor.dir/vnd.com.rajora.arun.chit.chatdevelopers.bots.provider.bot";
       }
       else if(sUriMatcher.match(uri)==2){
            return "vnd.android.cursor.item/vnd.com.rajora.arun.chit.chatdevelopers.bots.provider.bot";
       }
        else {
           return null;
       }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db=mBotDatabase.getWritableDatabase();
        long rowId=db.insert(BotContracts.TABLE_NAME,"",values);
        if (rowId > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI,rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add item(s) into " + uri);
    }

    @Override
    public boolean onCreate() {
        mBotDatabase=new BotDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        db=mBotDatabase.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        queryBuilder.setTables(BotContracts.TABLE_NAME);
        switch (sUriMatcher.match(uri)){
            case 1:
                queryBuilder.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;
            case 2:
                queryBuilder.appendWhere( BotContracts.COLUMN_ID+ "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
           sortOrder=BotContracts.COLUMN_BOT_NAME;
        }
        Cursor c = queryBuilder.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        db=mBotDatabase.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case 1:
                count = db.update(BotContracts.TABLE_NAME, values, selection, selectionArgs);
                break;

            case 2:
                count = db.update(BotContracts.TABLE_NAME, values, BotContracts.COLUMN_ID+ " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
