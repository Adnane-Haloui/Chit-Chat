package com.rajora.arun.chat.chit.chitchatdevelopers.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotProviderHelper;
import com.rajora.arun.chat.chit.chitchatdevelopers.dataModel.GlobalBotDataModel;
import com.rajora.arun.chat.chit.chitchatdevelopers.dataModel.LocalBotDataModel;
import com.rajora.arun.chat.chit.chitchatdevelopers.utils.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class UploadBotDetails extends IntentService {
    private static final String PARAM_PIC = "PROFILE";
    private static final String PARAM_NAME = "NAME";
    private static final String PARAM_ABOUT = "ABOUT";
    private static final String PARAM_URL = "PHONE_NUMBER";
    private static final String PARAM_SECRET = "PHONE_NUMBER";
	private static final String PARAM_GID = "GID";
	private static final String PARAM_ID = "ID";

    private static final String ACTION_CREATE="CREATE_ACTION";
	private static final String ACTION_UPDATE="UPDATE_ACTION";

	public UploadBotDetails() {
        super("UploadProfileDetails");
    }

    public static void startUploadBot(Context context, String url, String  pic, String name, String about,String secret) {
        Intent intent = new Intent(context, UploadBotDetails.class);
	    intent.setAction(ACTION_CREATE);
        intent.putExtra(PARAM_PIC, pic);
        intent.putExtra(PARAM_NAME, name);
        intent.putExtra(PARAM_ABOUT,about);
        intent.putExtra(PARAM_URL,url);
        intent.putExtra(PARAM_SECRET,secret);
        context.startService(intent);
    }

	public static void startUpdateBot(Context context, String gid,String id, String url, String pic, String name, String about,String secret) {
		Intent intent = new Intent(context, UploadBotDetails.class);
		intent.setAction(ACTION_UPDATE);
		intent.putExtra(PARAM_GID,PARAM_ID);
		intent.putExtra(PARAM_PIC, pic);
		intent.putExtra(PARAM_NAME, name);
		intent.putExtra(PARAM_ABOUT,about);
		intent.putExtra(PARAM_GID,gid);
		intent.putExtra(PARAM_ID,id);
		intent.putExtra(PARAM_URL,url);
		intent.putExtra(PARAM_SECRET,secret);
		context.startService(intent);
	}

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
	        FirebaseDatabase mFirebaseDatabase=FirebaseDatabase.getInstance();
	        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);

	        final String ph_no=sharedPreferences.getString("phone","null").substring(1);
	        final String dev_name=sharedPreferences.getString("name","null");
	        final String pic = intent.getStringExtra(PARAM_PIC);
	        final String name = intent.getStringExtra(PARAM_NAME);
	        final String about = intent.getStringExtra(PARAM_ABOUT);
	        final String url=intent.getStringExtra(PARAM_URL);
	        final String secret=intent.getStringExtra(PARAM_SECRET);

	        DatabaseReference itemDatabaseReference=mFirebaseDatabase.getReference("botItems/"+ph_no+"/");
	        DatabaseReference listDatabaseReference=mFirebaseDatabase.getReference("botList/");

	        final String key;
	        final String g_key;
	        if(intent.getAction().equals(ACTION_UPDATE)){
				g_key=intent.getStringExtra(PARAM_GID);
		        key=intent.getStringExtra(PARAM_ID);
	        }
	        else{
		        key= itemDatabaseReference.push().getKey().substring(1);
		        g_key=listDatabaseReference.push().getKey().substring(1);

	        }
	        long timestamp=utils.getCurrentTimestamp();
	        itemDatabaseReference=itemDatabaseReference.child(key);
	        listDatabaseReference=listDatabaseReference.child(g_key);
	        final DatabaseReference ref=itemDatabaseReference;
	        final DatabaseReference gref=listDatabaseReference;
	        GlobalBotDataModel gitem=new GlobalBotDataModel(key,g_key,name,about,dev_name,url,secret,null,timestamp,timestamp,false);
	        LocalBotDataModel litem=new LocalBotDataModel(key,g_key,name,about,ph_no,dev_name,url,secret,null,timestamp,timestamp,false);

	        if(intent.getAction().equals(ACTION_UPDATE)){
		        if(pic==null){
			        BotProviderHelper.UpdateBot(getApplicationContext(),litem);
		        }
		        else{
			        BotProviderHelper.UpdateBot(getApplicationContext(),litem,pic);
		        }
	        }
	        else{
		        if(pic==null){
			        BotProviderHelper.AddBot(getApplicationContext(),litem);
		        }
		        else{
			        BotProviderHelper.AddBot(getApplicationContext(),litem,pic);
		        }

	        }
            gref.setValue(gitem);
            ref.setValue(litem);
	        if(pic!=null){
		        try {
			        final StorageReference ProfilePicRef = FirebaseStorage.getInstance().getReference().child("botItem/" + g_key + "/botpic.png");
			        InputStream stream=getContentResolver().openInputStream(Uri.parse(pic));
			        if(stream!=null){
				        ProfilePicRef.putStream(stream);
			        }
		        } catch (FileNotFoundException e) {
			        e.printStackTrace();
		        }

	        }

        }
    }
}
