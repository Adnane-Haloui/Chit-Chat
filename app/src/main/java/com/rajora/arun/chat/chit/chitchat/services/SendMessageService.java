package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ProviderHelper;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.FirebaseUtils;

import java.io.FileNotFoundException;

public class SendMessageService extends IntentService {

    private static final String EXTRA_FROM_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.FROM_ID";
    private static final String EXTRA_TO_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.TO_ID";
    private static final String EXTRA_CONTENT = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT";
    private static final String EXTRA_CONTENT_TYPE = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT_TYPE";
    private static final String EXTRA_TIMESTAMP = "com.rajora.arun.chat.chit.chitchat.services.extra.TIMESTAMP";
    private static final String EXTRA_IS_BOT = "com.rajora.arun.chat.chit.chitchat.services.extra.IS_BOT";

	FirebaseDatabase mFirebaseDatabase;

    public SendMessageService() {
        super("SendMessageService");
        mFirebaseDatabase=FirebaseDatabase.getInstance();
    }

    public static void startSendTextMessage(Context context, String from, String to, String content,String content_type,boolean is_bot,long timestamp) {
        Intent intent = new Intent(context, SendMessageService.class);
	    intent.putExtra(EXTRA_FROM_ID,from);
        intent.putExtra(EXTRA_TO_ID, to);
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
	        sendTextMessage(from,to,content,content_type,is_bot,timestamp);
        }
    }

    private void sendTextMessage(String from_id,String to_id,String message,String content_type,boolean is_bot,long timestamp){
	    ChatItemDataModel item=new ChatItemDataModel(to_id,is_bot,message,"sent","read",content_type,timestamp);
	    item.chat_id= FirebaseUtils.sendChatMessageToFirebase(mFirebaseDatabase,item,from_id);
	    ProviderHelper.handleMessageInDatabase(this,item);
    }
}
