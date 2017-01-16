package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ProviderHelper;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.FirebaseUtils;

public class SendMessageService extends IntentService {

    private static final String EXTRA_FROM_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.FROM_ID";
    private static final String EXTRA_TO_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.TO_ID";
    private static final String EXTRA_CONTENT = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT";
    private static final String EXTRA_CONTENT_TYPE = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT_TYPE";
    private static final String EXTRA_TIMESTAMP = "com.rajora.arun.chat.chit.chitchat.services.extra.TIMESTAMP";
    private static final String EXTRA_IS_BOT = "com.rajora.arun.chat.chit.chitchat.services.extra.IS_BOT";
	private static final String EXTRA_FILE_URI="com.rajora.arun.chat.chit.chitchat.services.extra.FILE_URI";

	private static final String ACTION_UPLOAD_FILE="FILE_UPLOAD_ACTION";
	private static final String ACTION_UPLOAD_TEXT="TEXT_UPLOAD_ACTION";

	FirebaseDatabase mFirebaseDatabase;


    public SendMessageService() {
        super("SendMessageService");
        mFirebaseDatabase=FirebaseDatabase.getInstance();
    }

    public static void startSendTextMessage(Context context, String from, String to, String content,String content_type,boolean is_bot,long timestamp) {
        Intent intent = new Intent(context, SendMessageService.class);
	    intent.setAction(ACTION_UPLOAD_TEXT);
        intent.putExtra(EXTRA_FROM_ID,from);
        intent.putExtra(EXTRA_TO_ID, to);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_CONTENT_TYPE, content_type);
        intent.putExtra(EXTRA_IS_BOT,is_bot);
        intent.putExtra(EXTRA_TIMESTAMP,timestamp);
        context.startService(intent);
    }

	public static void startSendFileMessage(Context context, String from, String to,String file_uri, String content,String content_type,boolean is_bot,long timestamp) {
		Intent intent = new Intent(context, SendMessageService.class);
		intent.setAction(ACTION_UPLOAD_FILE);
		intent.putExtra(EXTRA_FROM_ID,from);
		intent.putExtra(EXTRA_TO_ID, to);
		intent.putExtra(EXTRA_FILE_URI,file_uri);
		intent.putExtra(EXTRA_CONTENT, content);
		intent.putExtra(EXTRA_CONTENT_TYPE, content_type);
		intent.putExtra(EXTRA_IS_BOT,is_bot);
		intent.putExtra(EXTRA_TIMESTAMP,timestamp);
		context.startService(intent);
	}


	@Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String from = intent.getStringExtra(EXTRA_FROM_ID);
            String to = intent.getStringExtra(EXTRA_TO_ID);
            String content = intent.getStringExtra(EXTRA_CONTENT);
            String content_type = intent.getStringExtra(EXTRA_CONTENT_TYPE);
            boolean is_bot = intent.getBooleanExtra(EXTRA_IS_BOT,false);
            long timestamp = intent.getLongExtra(EXTRA_TIMESTAMP,-1);
	        if(intent.getAction().equals(ACTION_UPLOAD_TEXT)){
		        sendTextMessage(from,to,content,content_type,is_bot,timestamp);
	        }
	        else{
		        sendFileMessage(from,to,content,intent.getStringExtra(EXTRA_FILE_URI),content_type,is_bot,timestamp);
	        }
        }
    }
    private void sendTextMessage(String from_id,String to_id,String message,String content_type,boolean is_bot,long timestamp){
	    ChatItemDataModel item=new ChatItemDataModel(to_id,is_bot,message,"sent","read",content_type,timestamp);
	    item.chat_id= FirebaseUtils.sendChatMessageToFirebase(mFirebaseDatabase,item,from_id);
	    ProviderHelper.handleMessageInDatabase(this,item);
    }

    private void sendFileMessage(String from_id,String to_id,String uri,String file_details,String content_type,boolean is_bot,long timestamp){

    }

}
