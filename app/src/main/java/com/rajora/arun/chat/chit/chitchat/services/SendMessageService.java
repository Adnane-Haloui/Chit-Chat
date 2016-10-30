package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chats;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

import java.sql.Ref;
import java.util.HashMap;

public class SendMessageService extends IntentService {
    private static final String ACTION_SEND_MESSAGE_TEXT_TO_USER = "com.rajora.arun.chat.chit.chitchat.services.action.SEND_MESSAGE_TEXT_TO_USER";
    private static final String ACTION_SEND_MESSAGE_TEXT_TO_BOT = "com.rajora.arun.chat.chit.chitchat.services.action.SEND_MESSAGE_TEXT_TO_BOT";

    private static final String EXTRA_FROM_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.FROM_ID";
    private static final String EXTRA_TO_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.TO_ID";
    private static final String EXTRA_CONTENT_TYPE = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT_TYPE";
    private static final String EXTRA_CONTENT = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT";
    private static final String EXTRA_TIMESTAMP = "com.rajora.arun.chat.chit.chitchat.services.extra.TIMESTAMP";
    private static final String EXTRA_BOT_DETAILS = "com.rajora.arun.chat.chit.chitchat.services.extra.BOT_DETAILS";
    FirebaseDatabase mFirebaseDatabase;


    public SendMessageService() {
        super("SendMessageService");
        mFirebaseDatabase=FirebaseDatabase.getInstance();
    }

    public static void startSendTextMessageToUser(Context context,String from, String to,String content_type,String content,long timestamp) {
        Intent intent = new Intent(context, SendMessageService.class);
        intent.setAction(ACTION_SEND_MESSAGE_TEXT_TO_USER);
        intent.putExtra(EXTRA_FROM_ID,from);
        intent.putExtra(EXTRA_TO_ID, to);
        intent.putExtra(EXTRA_CONTENT_TYPE, content_type);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_TIMESTAMP,timestamp);
        context.startService(intent);
    }
    public static void startSendTextMessageToBot(Context context, String from, String to, String content_type, String content, long timestamp, Bundle bundle) {
        Intent intent = new Intent(context, SendMessageService.class);
        intent.setAction(ACTION_SEND_MESSAGE_TEXT_TO_BOT);
        intent.putExtra(EXTRA_FROM_ID,from);
        intent.putExtra(EXTRA_TO_ID, to);
        intent.putExtra(EXTRA_CONTENT_TYPE, content_type);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_TIMESTAMP,timestamp);
        intent.putExtra(EXTRA_BOT_DETAILS,bundle);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_MESSAGE_TEXT_TO_USER.equals(action)) {
                final String to = intent.getStringExtra(EXTRA_TO_ID);
                final String from = intent.getStringExtra(EXTRA_FROM_ID);
                final String content_type = intent.getStringExtra(EXTRA_CONTENT_TYPE);
                final String content = intent.getStringExtra(EXTRA_CONTENT);
                final long timestamp = intent.getLongExtra(EXTRA_TIMESTAMP,-1);
                sendTextMessageToUser(from,to,content_type,content,timestamp);
            }
            else if(ACTION_SEND_MESSAGE_TEXT_TO_BOT.equals(action)){
                final String to = intent.getStringExtra(EXTRA_TO_ID);
                final String from = intent.getStringExtra(EXTRA_FROM_ID);
                final String content_type = intent.getStringExtra(EXTRA_CONTENT_TYPE);
                final String content = intent.getStringExtra(EXTRA_CONTENT);
                final long timestamp = intent.getLongExtra(EXTRA_TIMESTAMP,-1);
                final Bundle bot_details=intent.getBundleExtra(EXTRA_BOT_DETAILS);
                sendTextMessageToBot(from,to,content_type,content,timestamp,bot_details);
            }
        }
    }
    private void sendTextMessageToBot(String from_number,String to_id,String content_type,String content,long timestamp,Bundle bot_details){
        bot_details.keySet();
        String bot_name=bot_details.getString("botName","");
        String bot_number=bot_details.getString("botNumber","");
        String bot_about=bot_details.getString("botAbout","");
        String bot_gid=bot_details.getString("botGid","");
        String bot_image_url=bot_details.getString("botImageUrl","");
        String bot_dev_name=bot_details.getString("botDevName","");

        Cursor historyStatusCursor=getContentResolver().query(ChatContentProvider.CHATS_URI,new String[]{
                contract_chats.COLUMN_ABOUT,
                contract_chats.COLUMN_ID,
                contract_chats.COLUMN_PIC,
                contract_chats.COLUMN_IS_BOT,
                contract_chats.COLUMN_BOT_ID,
                contract_chats.COLUMN_LAST_MESSAGE,
                contract_chats.COLUMN_LAST_MESSAGE_TIME,
                contract_chats.COLUMN_NAME,
                contract_chats.COLUMN_PH_NUMBER
        }, contract_chats.COLUMN_ID+" = ? ",new String[]{to_id},null);
        ContentValues values=new ContentValues();
        values.put(contract_chats.COLUMN_ID,to_id);
        values.put(contract_chats.COLUMN_BOT_ID,to_id);
        values.put(contract_chats.COLUMN_IS_BOT,true);
        values.put(contract_chats.COLUMN_LAST_MESSAGE,content);
        values.put(contract_chats.COLUMN_ABOUT,bot_about);
        values.put(contract_chats.COLUMN_BOT_PIC_URL,bot_image_url);
        values.put(contract_chats.COLUMN_BOT_DEV_NAME,bot_dev_name);
        values.put(contract_chats.COLUMN_NAME,bot_name);
        values.put(contract_chats.COLUMN_LAST_MESSAGE_TIME,timestamp);
        if(historyStatusCursor!=null && historyStatusCursor.getCount()>0 && historyStatusCursor.moveToFirst()
                && historyStatusCursor.getLong(historyStatusCursor.getColumnIndex(contract_chats.COLUMN_LAST_MESSAGE_TIME))<=timestamp){
            getContentResolver().update(ChatContentProvider.CHATS_URI,values,contract_chats.COLUMN_ID+" = ? ",new String[]{to_id});
        }
        else{
            getContentResolver().insert(ChatContentProvider.CHATS_URI,values);
        }

        DatabaseReference itemDatabaseReference=mFirebaseDatabase.getReference("botChatItems/"+to_id+"/");
        DatabaseReference revItemReference=mFirebaseDatabase.getReference("chatItems/"+from_number.substring(1)+"/");
        final String id_on_server=itemDatabaseReference.push().getKey().substring(1);
        itemDatabaseReference=itemDatabaseReference.child(id_on_server);

        ContentValues chatValues=new ContentValues();
        chatValues.put(contract_chat.COLUMN_IS_BOT,true);
        chatValues.put(contract_chat.COLUMN_MESSAGE,content);
        chatValues.put(contract_chat.COLUMN_MESSAGE_ID_ON_SERVER,id_on_server);
        chatValues.put(contract_chat.COLUMN_MESSAGE_SENDER_ID,to_id);
        chatValues.put(contract_chat.COLUMN_MESSAGE_SENDER_NUMBER,from_number);
        chatValues.put(contract_chat.COLUMN_MESSAGE_STATUS,"sending");
        chatValues.put(contract_chat.COLUMN_MESSAGE_TYPE,content_type);
        chatValues.put(contract_chat.COLUMN_MESSAGE_RECEIVER_ID,to_id);
        chatValues.put(contract_chat.COLUMN_TIMESTAMP,timestamp);
        getContentResolver().insert(ChatContentProvider.CHAT_URI,chatValues);

        HashMap<String,Object> fbvalues=new HashMap<String, Object>();
        fbvalues.put("sender",from_number);
        fbvalues.put("receiver",to_id);
        fbvalues.put("is_bot",true);
        fbvalues.put("type",content_type);
        fbvalues.put("content",content);
        fbvalues.put("id",id_on_server);
        fbvalues.put("timestamp",timestamp);
        fbvalues.put("g_timestamp", ServerValue.TIMESTAMP);
        revItemReference.push().updateChildren(fbvalues);
        itemDatabaseReference.updateChildren(fbvalues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                ContentValues UpdateStatusvalues=new ContentValues();
                UpdateStatusvalues.put(contract_chat.COLUMN_MESSAGE_STATUS,"sent");
                getContentResolver().update(ChatContentProvider.CHAT_URI,
                        UpdateStatusvalues,contract_chat.COLUMN_MESSAGE_ID_ON_SERVER+" = ?",
                        new String[]{id_on_server});
            }
        });

    }

    private void sendTextMessageToUser(String from_number,String to_id,String content_type,String content,long timestamp) {

        Cursor historyStatusCursor=getContentResolver().query(ChatContentProvider.CHATS_URI,new String[]{
                contract_chats.COLUMN_ABOUT,
                contract_chats.COLUMN_ID,
                contract_chats.COLUMN_PIC,
                contract_chats.COLUMN_IS_BOT,
                contract_chats.COLUMN_BOT_ID,
                contract_chats.COLUMN_LAST_MESSAGE,
                contract_chats.COLUMN_LAST_MESSAGE_TIME,
                contract_chats.COLUMN_NAME,
                contract_chats.COLUMN_PH_NUMBER
            }, contract_chats.COLUMN_ID+" = ? ",new String[]{to_id},null);

            if(historyStatusCursor!=null && historyStatusCursor.getCount()>0 ){
                if(historyStatusCursor.moveToFirst() && historyStatusCursor.getLong(historyStatusCursor
                        .getColumnIndex(contract_chats.COLUMN_LAST_MESSAGE_TIME))<=timestamp){
                    ContentValues values=new ContentValues();
                    values.put(contract_chats.COLUMN_ID,to_id);
                    values.put(contract_chats.COLUMN_IS_BOT,false);
                    values.put(contract_chats.COLUMN_LAST_MESSAGE,content);
                    values.put(contract_chats.COLUMN_LAST_MESSAGE_TIME,timestamp);
                    getContentResolver().update(ChatContentProvider.CHATS_URI,values,contract_chats.COLUMN_ID+" = ? ",new String[]{to_id});
                }

            }
            else{
                Cursor valueCursor=getContentResolver().query(ChatContentProvider.CONTACTS_URI,new String[]{
                    }, contract_contacts.COLUMN_PH_NUMBER+" = ? ",new String[]{to_id},null);
                if(valueCursor!=null && valueCursor.moveToFirst()){
                    ContentValues values=new ContentValues();
                    values.put(contract_chats.COLUMN_ID,valueCursor.getString(valueCursor.getColumnIndex(contract_contacts.COLUMN_PH_NUMBER)));
                    values.put(contract_chats.COLUMN_IS_BOT,false);
                    values.put(contract_chats.COLUMN_LAST_MESSAGE,content);
                    values.put(contract_chats.COLUMN_LAST_MESSAGE_TIME,timestamp);
                    values.put(contract_chats.COLUMN_NAME,valueCursor.getString(valueCursor.getColumnIndex(contract_contacts.COLUMN_NAME)));
                    values.put(contract_chats.COLUMN_PIC,valueCursor.getBlob(valueCursor.getColumnIndex(contract_contacts.COLUMN_PIC)));
                    values.put(contract_chats.COLUMN_PH_NUMBER,valueCursor.getString(valueCursor.getColumnIndex(contract_contacts.COLUMN_PH_NUMBER)));
                    getContentResolver().insert(ChatContentProvider.CHATS_URI,values);
                }
            }
            DatabaseReference itemDatabaseReference=mFirebaseDatabase.getReference("chatItems/"+to_id.substring(1)+"/");
            DatabaseReference revItemReference=mFirebaseDatabase.getReference("chatItems/"+from_number.substring(1)+"/");
            final String id_on_server=itemDatabaseReference.push().getKey().substring(1);
            itemDatabaseReference=itemDatabaseReference.child(id_on_server);

            ContentValues chatValues=new ContentValues();
            chatValues.put(contract_chat.COLUMN_IS_BOT,false);
            chatValues.put(contract_chat.COLUMN_MESSAGE,content);
            chatValues.put(contract_chat.COLUMN_MESSAGE_ID_ON_SERVER,id_on_server);
            chatValues.put(contract_chat.COLUMN_MESSAGE_SENDER_ID,to_id);
            chatValues.put(contract_chat.COLUMN_MESSAGE_SENDER_NUMBER,from_number);
            chatValues.put(contract_chat.COLUMN_MESSAGE_STATUS,"sending");
            chatValues.put(contract_chat.COLUMN_MESSAGE_TYPE,content_type);
            chatValues.put(contract_chat.COLUMN_MESSAGE_RECEIVER_ID,to_id);
            chatValues.put(contract_chat.COLUMN_TIMESTAMP,timestamp);
            getContentResolver().insert(ChatContentProvider.CHAT_URI,chatValues);

            HashMap<String,Object> values=new HashMap<String, Object>();
            values.put("sender",from_number);
            values.put("receiver",to_id);
            values.put("is_bot",false);
            values.put("type",content_type);
            values.put("content",content);
            values.put("id",id_on_server);
            values.put("timestamp",timestamp);
            values.put("g_timestamp", ServerValue.TIMESTAMP);
            revItemReference.push().updateChildren(values);
            itemDatabaseReference.updateChildren(values, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    ContentValues UpdateStatusvalues=new ContentValues();
                    UpdateStatusvalues.put(contract_chat.COLUMN_MESSAGE_STATUS,"sent");
                    getContentResolver().update(ChatContentProvider.CHAT_URI,
                            UpdateStatusvalues,contract_chat.COLUMN_MESSAGE_ID_ON_SERVER+" = ?",
                            new String[]{id_on_server});
                }
            });
    }

}
