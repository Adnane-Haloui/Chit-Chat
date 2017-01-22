package com.rajora.arun.chat.chit.chitchat.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.R.mipmap;
import com.rajora.arun.chat.chit.chitchat.activities.MainActivity;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractNotificationList;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractUnreadCount;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseChatItemDataModel;

import java.util.HashMap;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

import static android.support.v4.app.NotificationCompat.CATEGORY_MESSAGE;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

public class FetchNewChatData extends Service {

    private final customBinder mBinder= new customBinder();

    private FirebaseDatabase firebaseDatabase;

	private DatabaseReference onlineReference;
	private DatabaseReference connectedRef;
    private DatabaseReference mRef;

    private String currentItemId;
    private boolean is_currentItemId_bot;
    private String ph_no;
    private long last_time_stamp;

	ValueEventListener singleEventListener=new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			mRef.removeEventListener(singleEventListener);
			if(dataSnapshot!=null){
					new UpdateNewChatAsyncTask(true,true,FetchNewChatData.this).execute(dataSnapshot);
			}
			stopSelf();
		}

		@Override
		public void onCancelled(DatabaseError databaseError) {
			mRef.removeEventListener(singleEventListener);
			stopSelf();
		}
	};

    ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
	        new UpdateNewChatAsyncTask(false,false,FetchNewChatData.this).execute(dataSnapshot);
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

	ValueEventListener connectedEventListener=new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			if(dataSnapshot.getValue(Boolean.class)){
				DatabaseReference con=onlineReference.push();
				HashMap<String, Object> value = new HashMap<>();
				value.put("online",Boolean.TRUE);
				value.put("connect", ServerValue.TIMESTAMP);
				con.setValue(value);
				value.put("online",Boolean.FALSE);
				value.put("disconnect",ServerValue.TIMESTAMP);
				con.onDisconnect().updateChildren(value);
			}
		}

		@Override
		public void onCancelled(DatabaseError databaseError) {

		}
	};

    public FetchNewChatData() {
    }

    @Override
    public void onCreate() {
	    firebaseDatabase=FirebaseDatabase.getInstance();
	    SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
	    ph_no=sharedPreferences.getString("phone","1").substring(1);
	    last_time_stamp=sharedPreferences.getLong("lastSyncTimestamp",-1);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	    mRef=firebaseDatabase.getReference("chatItems/"+ph_no+"/");
	    if(mRef!=null){
		    if(last_time_stamp==-1){
			    mRef.orderByChild("g_timestamp").addListenerForSingleValueEvent(singleEventListener);
		    }
		    else{
			    mRef.orderByChild("g_timestamp").startAt(last_time_stamp).
					    addListenerForSingleValueEvent(singleEventListener);
		    }
	    }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
	    mRef=firebaseDatabase.getReference("chatItems/"+ph_no+"/");
	    if(last_time_stamp==-1){
		    mRef.orderByChild("g_timestamp").addChildEventListener(childEventListener);
	    }
	    else{
		    mRef.orderByChild("g_timestamp").startAt(last_time_stamp).
				    addChildEventListener(childEventListener);
	    }
	    onlineReference=firebaseDatabase.getReference("online/users/"+ph_no);
	    connectedRef=firebaseDatabase.getReference(".info/connected");
	    connectedRef.addValueEventListener(connectedEventListener);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        currentItemId=null;
	    is_currentItemId_bot=false;
	    if(connectedRef!=null){
		    connectedRef.removeEventListener(connectedEventListener);
	    }
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
	    mRef=firebaseDatabase.getReference("chatItems/"+ph_no+"/");
	    if(last_time_stamp==-1){
		    mRef.orderByChild("g_timestamp").addChildEventListener(childEventListener);
	    }
	    else{
		    mRef.orderByChild("g_timestamp").startAt(last_time_stamp).
				    addChildEventListener(childEventListener);
	    }
	    onlineReference=firebaseDatabase.getReference("online/users/"+ph_no);
	    connectedRef=firebaseDatabase.getReference(".info/connected");
	    connectedRef.addValueEventListener(connectedEventListener);
	    super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
	    if(mRef!=null){
            mRef.removeEventListener(childEventListener);
        }
    }

    public void setCurrentItem(String id,boolean is_bot){
        currentItemId=id;
        is_currentItemId_bot=is_bot;
    }

    public class customBinder extends Binder{
        public FetchNewChatData getService() {
            return FetchNewChatData.this;
        }
    }

	public class UpdateNewChatAsyncTask extends AsyncTask<DataSnapshot, Void, Void> {

		boolean mIsList;
		boolean mStopService;
		FetchNewChatData mContext;

		public UpdateNewChatAsyncTask(boolean is_list,boolean stopService,FetchNewChatData context) {
			mIsList=is_list;
			mStopService=stopService;
			mContext=context;
		}

		@Override
		protected Void doInBackground(DataSnapshot... dataSnapshots) {
			if(dataSnapshots.length>0 && dataSnapshots[0]!=null){
				DataSnapshot dataSnapshot=dataSnapshots[0];
				if(mIsList){
					for (DataSnapshot chatItem : dataSnapshot.getChildren()) {
						receiveMessage(chatItem.getValue(FirebaseChatItemDataModel.class),
								chatItem.getKey().substring(1));
					}
				}
				else{
					receiveMessage( dataSnapshot.getValue(FirebaseChatItemDataModel.class),
							dataSnapshot.getKey().substring(1));
				}
			}
			return null;
		}

		private void receiveMessage(FirebaseChatItemDataModel data,String key){
			if(!isChatItemReceived(data,key)){
				updateChat(data,key);
				if(!data.sender.equals(ph_no) && (currentItemId==null || !(data.sender.equals(currentItemId) && data.is_bot==is_currentItemId_bot))){
					sendNotification();
				}
			}
			if(data.is_bot && !isBotDetailsPresent(data)){
				updateBotDetails(data);
			}
		}

		private boolean isBotDetailsPresent(FirebaseChatItemDataModel data){
			Cursor mcursor=getContentResolver().query(ChatContentProvider.CONTACT_LIST_URI,
					new String[]{ContractContacts._ID},
					ContractChat.COLUMN_CONTACT_ID+ " = ? AND "+ContractChat.COLUMN_IS_BOT+" = ? ",
					new String[]{data.sender.substring(1).equals(ph_no)?
							data.receiver:data.sender,"1"},null);
			if(mcursor!=null && mcursor.getCount()>0){
				if(!mcursor.isClosed())
					mcursor.close();
				return true;
			}
			if(mcursor!=null && !mcursor.isClosed())
				mcursor.close();
			return false;
		}

		private void updateBotDetails(FirebaseChatItemDataModel data){
			String bot_id=data.sender.substring(1).equals(ph_no)?data.receiver:data.sender;
			DatabaseReference botRef=firebaseDatabase.getInstance().getReference("botList/"+bot_id);
			botRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot!=null && dataSnapshot.exists()){
						FirebaseBotsDataModel botDetails=dataSnapshot.getValue(FirebaseBotsDataModel.class);
						UpdateBotDetailsIntentService.startBotDetailsUpdate(mContext,botDetails);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}

		private boolean isChatItemReceived(FirebaseChatItemDataModel data,String key){
			Cursor mcursor=getContentResolver().query(ChatContentProvider.CHAT_URI,
					new String[]{ContractChat.COLUMN_CHAT_ID},
					ContractChat.COLUMN_CHAT_ID+" = ? AND "+ContractChat.COLUMN_CONTACT_ID+
							" = ? AND "+ContractChat.COLUMN_IS_BOT+" = ? ",
					new String[]{key,data.sender.substring(1).equals(ph_no)?
							data.receiver:data.sender,data.is_bot?"1":"0"},null);
			if(mcursor!=null && mcursor.getCount()>0){
				if(!mcursor.isClosed())
					mcursor.close();
				return true;
			}
			if(mcursor!=null && !mcursor.isClosed())
				mcursor.close();
			return false;
		}

		private void updateChat(FirebaseChatItemDataModel data, String key) {
			ContentValues values=new ContentValues();

			values.put(ContractChat.COLUMN_CHAT_ID,key);
			values.put(ContractChat.COLUMN_IS_BOT,data.is_bot);
			values.put(ContractChat.COLUMN_MESSAGE,data.content);
			values.put(ContractChat.COLUMN_MESSAGE_TYPE,data.type);
			values.put(ContractChat.COLUMN_TIMESTAMP,data.g_timestamp);
			if(data.sender.substring(1).equals(ph_no)){
				values.put(ContractChat.COLUMN_CONTACT_ID,data.receiver);
				values.put(ContractChat.COLUMN_MESSAGE_DIRECTION,"sent");
				values.put(ContractChat.COLUMN_MESSAGE_STATUS,"read");
			}
			else{
				values.put(ContractChat.COLUMN_CONTACT_ID,data.sender);
				values.put(ContractChat.COLUMN_MESSAGE_DIRECTION,"received");
				values.put(ContractChat.COLUMN_MESSAGE_STATUS, currentItemId!=null && data.sender.equals(currentItemId)
						&& is_currentItemId_bot==data.is_bot ?"read":"unread");
			}
			getContentResolver().insert(ChatContentProvider.CHAT_URI,values);
			SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
			long time_stamp=sharedPreferences.getLong("lastSyncTimestamp",-1);
			if(time_stamp==-1 || time_stamp<data.g_timestamp){
				sharedPreferences.edit().putLong("lastSyncTimestamp",data.g_timestamp).commit();
			}

		}


		private void sendNotification(){
			Cursor countCursor=mContext.getContentResolver().query(ChatContentProvider.UNREAD_COUNT_URI,
					new String[]{ContractUnreadCount.COLUMN_UNREAD_COUNT},null,null,null);
			if(countCursor!=null && countCursor.getCount()>0 && countCursor.moveToFirst()){
				long unreadCount=countCursor.getLong(countCursor.getColumnIndex(ContractUnreadCount.COLUMN_UNREAD_COUNT));
				if(unreadCount>0){
					Cursor contentCursor=mContext.getContentResolver().query(ChatContentProvider.NOTIFICATION_LIST_URI,
							new String[]{ContractNotificationList.COLUMN_CONTACT_ID,
									ContractNotificationList.COLUMN_IS_BOT,
									ContractNotificationList.COLUMN_MESSAGE,
									ContractNotificationList.COLUMN_MESSAGE_TIMESTAMP,
									ContractNotificationList.COLUMN_MESSAGE_TYPE,
									ContractNotificationList.COLUMN_NAME,},null,null,ContractNotificationList.COLUMN_MESSAGE_TIMESTAMP);
					if(contentCursor!=null && contentCursor.moveToFirst()){
						NotificationCompat.MessagingStyle messagingStyle=new NotificationCompat.MessagingStyle("You");

						messagingStyle.setConversationTitle(unreadCount+" new messages.");
						do{
							String name=contentCursor.getString(contentCursor.getColumnIndex(ContractNotificationList.COLUMN_NAME));
							String contact_id=contentCursor.getString(contentCursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
							Boolean is_bot=contentCursor.getInt(contentCursor.getColumnIndex(ContractNotificationList.COLUMN_IS_BOT))!=0;
							String message=contentCursor.getString(contentCursor.getColumnIndex(ContractNotificationList.COLUMN_MESSAGE));
							String message_type=contentCursor.getString(contentCursor.getColumnIndex(ContractNotificationList.COLUMN_MESSAGE_TYPE));
							long timestamp=contentCursor.getLong(contentCursor.getColumnIndex(ContractNotificationList.COLUMN_MESSAGE_TIMESTAMP));
							String sender=name==null || name.isEmpty()? is_bot?"Unknown bot":contact_id :name;
							String notificaion_message=message_type.equals("text")?message:"["+message_type+"]";
							messagingStyle.addMessage(notificaion_message,timestamp,sender);
						}while (contentCursor.moveToNext());
						android.support.v4.app.NotificationCompat.Builder notiBuilder=new NotificationCompat.Builder(mContext)
								.setSmallIcon(mipmap.ic_launcher)
								.setContentTitle(unreadCount+" new messages.")
								.setStyle(messagingStyle)
								.setAutoCancel(true)
								.setNumber((int) unreadCount)
								.setTicker(unreadCount+" new messages.")
								.setPriority(PRIORITY_HIGH)
								.setCategory(CATEGORY_MESSAGE)
								.setShowWhen(true)
								.setLights(Color.GREEN, 100, 200)
								.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
						Intent resultIntent = new Intent(mContext, MainActivity.class);
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
						stackBuilder.addParentStack(MainActivity.class);
						stackBuilder.addNextIntent(resultIntent);

						PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
								PendingIntent.FLAG_UPDATE_CURRENT);
						notiBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
						mNotificationManager.notify(100, notiBuilder.build());

					}
					if(contentCursor!=null && !contentCursor.isClosed()){
						contentCursor.close();
					}
				}
			}
			if(countCursor!=null && !countCursor.isClosed()){
				countCursor.close();
			}


		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if(mStopService){
				mContext.stopSelf();
			}
		}
	}


}
