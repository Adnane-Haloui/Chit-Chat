package com.rajora.arun.chat.chit.chitchat.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.activities.MainActivity;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chats;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;


public class FetchNewChatData extends Service {

    private customBinder mBinder= new customBinder();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private String currentItemId;
    private boolean is_bound=false;
    private String ph_no;
    private long last_time_stamp;

    ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            receiveMessage(( dataSnapshot.getValue(ChatItemDataModel.class)),dataSnapshot.getKey().substring(1));
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void receiveMessage(ChatItemDataModel data,String key){
        if(!isChatItemReceived(data,key)){
            updateDatabase(data,key);
            sendNotification(data,key);
        }
        if(data.getG_timestamp()>=last_time_stamp)
            last_time_stamp=data.getG_timestamp();
    }
    private boolean isChatItemReceived(ChatItemDataModel data,String key){
        Cursor mcursor=getContentResolver().query(ChatContentProvider.CHAT_URI,
                new String[]{contract_chat.COLUMN_MESSAGE_ID_ON_SERVER},contract_chat.COLUMN_MESSAGE_ID_ON_SERVER+" = ? ",
                new String[]{key},null);
        if(mcursor!=null && mcursor.getCount()>0){
            if(!mcursor.isClosed())
                mcursor.close();
            return true;
        }
        if(mcursor!=null && !mcursor.isClosed())
            mcursor.close();
        return false;
    }
    private void updateDatabase(ChatItemDataModel data,String key){
        updateLastMessage(data,key);
        updateChat(data,key);
    }

    private void updateLastMessage(ChatItemDataModel data, String key) {
        Cursor mcursor=getContentResolver().query(ChatContentProvider.CHATS_URI,
                new String[]{contract_chats.COLUMN_LAST_MESSAGE_TIME,contract_chats.COLUMN_ID},
                contract_chats.COLUMN_ID+" = ? OR "+contract_chats.COLUMN_ID+" = ? ",
                new String[]{data.getReceiver(),data.getSender()},null);
        if(mcursor!=null && mcursor.getCount()>0){
            mcursor.moveToFirst();
            if(mcursor.getLong(mcursor.getColumnIndex(contract_chats.COLUMN_LAST_MESSAGE_TIME))<=data.getG_timestamp()){
                ContentValues values=new ContentValues();
                values.put(contract_chats.COLUMN_LAST_MESSAGE_TIME,data.getG_timestamp());
                values.put(contract_chats.COLUMN_LAST_MESSAGE,data.getContent());
                getContentResolver().update(ChatContentProvider.CHATS_URI,values,
                        contract_chats.COLUMN_ID+" = ? OR "+contract_chats.COLUMN_ID+" = ? ",
                        new String[]{data.getReceiver(),data.getSender()});
            }
        }
        else{
            ContentValues values=new ContentValues();
            if(data.is_bot()){
                values.put(contract_chats.COLUMN_BOT_ID,data.getReceiver()==ph_no?data.getSender():data.getReceiver());
            }
            else{
                values.put(contract_chats.COLUMN_PH_NUMBER,data.getReceiver()==ph_no?data.getSender():data.getReceiver());
            }
            values.put(contract_chats.COLUMN_ID,data.getReceiver()==ph_no?data.getSender():data.getReceiver());
            values.put(contract_chats.COLUMN_IS_BOT,data.is_bot());
            values.put(contract_chats.COLUMN_LAST_MESSAGE,data.getContent());
            values.put(contract_chats.COLUMN_LAST_MESSAGE_TIME,data.getG_timestamp());
            values.put(contract_chats.COLUMN_NAME,data.getReceiver()==ph_no?data.getSender():data.getReceiver());

            getContentResolver().insert(ChatContentProvider.CHATS_URI,values);
        }
        if(mcursor!=null && !mcursor.isClosed())
            mcursor.close();
    }

    private void updateChat(ChatItemDataModel data, String key) {
        ContentValues values=new ContentValues();

        values.put(contract_chat.COLUMN_IS_BOT,data.is_bot());
        values.put(contract_chat.COLUMN_MESSAGE,data.getContent());
        values.put(contract_chat.COLUMN_MESSAGE_ID_ON_SERVER,key);
        values.put(contract_chat.COLUMN_MESSAGE_SENDER_ID,data.getSender());
        values.put(contract_chat.COLUMN_MESSAGE_SENDER_NUMBER,data.getSender());
        values.put(contract_chat.COLUMN_MESSAGE_STATUS,"received");
        values.put(contract_chat.COLUMN_MESSAGE_TYPE,data.getType());
        values.put(contract_chat.COLUMN_MESSAGE_RECEIVER_ID,data.getReceiver());
        values.put(contract_chat.COLUMN_TIMESTAMP,data.getG_timestamp());

        getContentResolver().insert(ChatContentProvider.CHAT_URI,values);
    }


    private void sendNotification(ChatItemDataModel data,String key){
        if(!data.getSender().equals(ph_no) && (currentItemId==null || data.getReceiver().equals(currentItemId))){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New Message Received")
                            .setContentText("Someone just sent you a message.");
            Intent resultIntent = new Intent(this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(100, mBuilder.build());
        }
    }

    public FetchNewChatData() {
    }

    @Override
    public void onCreate() {
        firebaseDatabase=FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        ph_no=sharedPreferences.getString("phone","");
        mRef=firebaseDatabase.getReference("chatItems/"+ph_no+"/");
        last_time_stamp=sharedPreferences.getLong("lastSyncTimestamp",-1);
        if(last_time_stamp==-1){
            mRef.orderByChild("g_timestamp").addChildEventListener(childEventListener);
        }
        else{
            mRef.orderByChild("g_timestamp").startAt(last_time_stamp+1).addChildEventListener(childEventListener);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        is_bound=true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        is_bound=false;
        currentItemId=null;
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        is_bound=true;
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        if(mRef!=null){
            mRef.removeEventListener(childEventListener);
        }
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong("lastSyncTimestamp",last_time_stamp);
        editor.commit();
    }

    public void setCurrentItemId(String id){
        currentItemId=id;
    }

    public class customBinder extends Binder{
        public FetchNewChatData getService() {
            return FetchNewChatData.this;
        }
    }

}
