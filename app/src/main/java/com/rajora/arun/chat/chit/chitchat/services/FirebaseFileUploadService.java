package com.rajora.arun.chat.chit.chitchat.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ProviderHelper;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.FirebaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class FirebaseFileUploadService extends Service {

	private static final String EXTRA_FROM_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.FROM_ID";
	private static final String EXTRA_TO_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.TO_ID";
	private static final String EXTRA_CONTENT = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT";
	private static final String EXTRA_CONTENT_TYPE = "com.rajora.arun.chat.chit.chitchat.services.extra.CONTENT_TYPE";
	private static final String EXTRA_TIMESTAMP = "com.rajora.arun.chat.chit.chitchat.services.extra.TIMESTAMP";
	private static final String EXTRA_IS_BOT = "com.rajora.arun.chat.chit.chitchat.services.extra.IS_BOT";
	private static final String EXTRA_FILE_URI = "com.rajora.arun.chat.chit.chitchat.services.extra.FILE_URI";
	private static final String EXTRA_FILE_NAME = "com.rajora.arun.chat.chit.chitchat.services.extra.FILE_NAME";
	private static final String EXTRA_CHAT_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.IS_BOT";
	private static final String EXTRA_FILE_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.FILE_URI";
	private static final String EXTRA_USER_CONTACT_ID = "com.rajora.arun.chat.chit.chitchat.services.extra.FILE_NAME";

	private static final String ACTION_FILE_UPLOAD="com.rajora.arun.chat.chit.chitchat.services.action.FILE_UPLOAD";
	private static final String ACTION_FILE_DOWNLOAD="com.rajora.arun.chat.chit.chitchat.services.action.FILE_DOWNLOAD";

	FirebaseDatabase mFirebaseDatabase;

	public FirebaseFileUploadService() {
		mFirebaseDatabase=FirebaseDatabase.getInstance();
	}

	public static void startSendFileMessage(Context context, String from, String to, String fileName, String file_uri, String content, String content_type, boolean is_bot, long timestamp) {
		Intent intent = new Intent(context, FirebaseFileUploadService.class);
		intent.putExtra(EXTRA_FROM_ID,from);
		intent.putExtra(EXTRA_TO_ID, to);
		intent.putExtra(EXTRA_FILE_URI,file_uri);
		intent.putExtra(EXTRA_CONTENT, content);
		intent.putExtra(EXTRA_CONTENT_TYPE, content_type);
		intent.putExtra(EXTRA_IS_BOT,is_bot);
		intent.putExtra(EXTRA_TIMESTAMP,timestamp);
		intent.putExtra(EXTRA_FILE_NAME,fileName);
		intent.setAction(ACTION_FILE_UPLOAD);
		context.startService(intent);
	}

	public static void startReceiveFileMessage(Context context, String from, String to, String chat_id, String file_name, String file_id, String content_type,String contact_id) {
		Intent intent = new Intent(context, FirebaseFileUploadService.class);
		intent.putExtra(EXTRA_FROM_ID,from);
		intent.putExtra(EXTRA_TO_ID, to);
		intent.putExtra(EXTRA_CHAT_ID,chat_id);
		intent.putExtra(EXTRA_FILE_NAME, file_name);
		intent.putExtra(EXTRA_FILE_ID, file_id);
		intent.putExtra(EXTRA_CONTENT_TYPE, content_type);
		intent.putExtra(EXTRA_USER_CONTACT_ID, contact_id);
		intent.setAction(ACTION_FILE_DOWNLOAD);
		context.startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String from = intent.getStringExtra(EXTRA_FROM_ID);
			String to = intent.getStringExtra(EXTRA_TO_ID);
			String fileName=intent.getStringExtra(EXTRA_FILE_NAME);
			String content_type = intent.getStringExtra(EXTRA_CONTENT_TYPE);
			if(intent.getAction().equals(ACTION_FILE_UPLOAD)){
				String content = intent.getStringExtra(EXTRA_CONTENT);
				String fileUri=intent.getStringExtra(EXTRA_FILE_URI);
				boolean is_bot = intent.getBooleanExtra(EXTRA_IS_BOT,false);
				long timestamp = intent.getLongExtra(EXTRA_TIMESTAMP,-1);
				sendFileMessage(startId,from,to,fileName,fileUri,content,content_type,is_bot,timestamp);
			}
			else{
				String chat_id = intent.getStringExtra(EXTRA_CHAT_ID);
				String file_id=intent.getStringExtra(EXTRA_FILE_ID);
				String user_contact_id = intent.getStringExtra(EXTRA_USER_CONTACT_ID);
				downloadFIle(startId,from,to,chat_id,fileName,file_id,content_type,user_contact_id);
			}

		}
		else{
			stopSelf(startId);
		}
		return START_REDELIVER_INTENT;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void sendFileMessage(final int serviceStartId,final String from_id,final String to_id,final String fileName,final String uri,final String file_details,final String content_type,final boolean is_bot,final long timestamp){
		final ChatItemDataModel item=new ChatItemDataModel(to_id,is_bot,file_details,"sent","read",content_type,timestamp);
		item.upload_status="preparing";
		item.extra_uri=uri;
		final String firebase_push_id= FirebaseUtils.getUniqueChatIdForFile(mFirebaseDatabase,item,from_id);
		item.chat_id=firebase_push_id;
		try {
			JSONObject msg=new JSONObject(file_details);
			msg.put("location",firebase_push_id);
			item.message=msg.toString();
		} catch (JSONException e) {
			//cannot happen guaranteed json is correct.
		}
		ProviderHelper.handleMessageInDatabase(this,item);
		StorageReference mStorageReference= FirebaseStorage.getInstance().getReference().child(from_id.substring(1)+"/"+to_id.substring(1)+"/"+item.chat_id+fileName);
		ProviderHelper.updateFileMessageInDatabase(this,firebase_push_id,item.contact_id,item.is_bot,"uploading");
		final ChatItemDataModel nChatItemDataModel=item;
		try {
			mStorageReference.putStream(getContentResolver().openInputStream(Uri.parse(uri))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					ProviderHelper.updateFileMessageInDatabase(FirebaseFileUploadService.this,firebase_push_id,item.contact_id,item.is_bot,"uploaded");
					FirebaseUtils.sendFileTextMessageToFirebase(mFirebaseDatabase,nChatItemDataModel,from_id);
					stopSelf(serviceStartId);
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					ProviderHelper.updateFileMessageInDatabase(FirebaseFileUploadService.this,firebase_push_id,item.contact_id,item.is_bot,"upload_failed");
					stopSelf(serviceStartId);
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void downloadFIle(final int serviceStartId,final String from_id,final String to_id,final String chat_id,final String fileName,final String file_id,final String file_type,final String userContactId){
		StorageReference mref=FirebaseStorage.getInstance().getReference().child(from_id.substring(1)+"/"+to_id.substring(1)+"/"+file_id+fileName);
		final File file=createFile(file_type,file_id+fileName);
		mref.getFile(file)
				.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
						Uri file_uri=FileProvider.getUriForFile(FirebaseFileUploadService.this,
								"com.rajora.arun.chat.chit.chitchat.fileprovider", file);
						String send_to=from_id;
						if(from_id.equals(userContactId)) send_to=to_id;
						ProviderHelper.updateFileMessageInDatabase(FirebaseFileUploadService.this,to_id,chat_id,"downloaded", file_uri.toString());
						Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						mediaScanIntent.setData(file_uri);
						sendBroadcast(mediaScanIntent);
						stopSelf();
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						String send_to=from_id;
						if(from_id.equals(userContactId)) send_to=to_id;
						ProviderHelper.updateFileMessageInDatabase(FirebaseFileUploadService.this,to_id,chat_id,"download_failed", null);
						stopSelf();
					}
				});

	}

	private File createFile(final String file_type,String fileName){
		File storageDir=null;
		if(file_type.equals("image")){
			storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		}
		else if(file_type.equals("video")){
			storageDir=getExternalFilesDir(Environment.DIRECTORY_MOVIES);
		}
		else{
			storageDir=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		}
		return new File(storageDir,fileName);
	}
}
