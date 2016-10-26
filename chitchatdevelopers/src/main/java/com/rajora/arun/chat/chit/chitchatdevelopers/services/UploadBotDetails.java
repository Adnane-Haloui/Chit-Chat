package com.rajora.arun.chat.chit.chitchatdevelopers.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
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
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotContentProvider;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotProviderHelper;
import com.rajora.arun.chat.chit.chitchatdevelopers.dataModel.BotDataModel;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;
import com.rajora.arun.chat.chit.chitchatdevelopers.utils.utils;

import java.util.HashMap;

public class UploadBotDetails extends IntentService {
    private static final String PARAM_PIC = "PROFILE";
    private static final String PARAM_NAME = "NAME";
    private static final String PARAM_ABOUT = "ABOUT";
    private static final String PARAM_URL = "PHONE_NUMBER";
    private static final String PARAM_SECRET = "PHONE_NUMBER";

    public UploadBotDetails() {
        super("UploadProfileDetails");
    }

    public static void startUploadBot(Context context, String url, byte pic[], String name, String about,String secret) {
        Intent intent = new Intent(context, UploadBotDetails.class);
        intent.putExtra(PARAM_PIC, pic);
        intent.putExtra(PARAM_NAME, name);
        intent.putExtra(PARAM_ABOUT,about);
        intent.putExtra(PARAM_URL,url);
        intent.putExtra(PARAM_SECRET,secret);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final byte[] pic = intent.getByteArrayExtra(PARAM_PIC);
            final String name = intent.getStringExtra(PARAM_NAME);
            final String about = intent.getStringExtra(PARAM_ABOUT);
            final String url=intent.getStringExtra(PARAM_URL);
            final String secret=intent.getStringExtra(PARAM_SECRET);

            FirebaseDatabase mFirebaseDatabase;
            mFirebaseDatabase=FirebaseDatabase.getInstance();
            final FirebaseStorage mStorage = FirebaseStorage.getInstance();

            SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
            final String ph_no=sharedPreferences.getString("phone","null");
            final String dev_name=sharedPreferences.getString("name","null");

            DatabaseReference itemDatabaseReference=mFirebaseDatabase.getReference("botItems/"+ph_no+"/");
            DatabaseReference listDatabaseReference=mFirebaseDatabase.getReference("botList/");

            final String key = itemDatabaseReference.push().getKey().substring(1);
            final String g_key=listDatabaseReference.push().getKey().substring(1);

            itemDatabaseReference=itemDatabaseReference.child(key);
            listDatabaseReference=listDatabaseReference.child(g_key);
            final DatabaseReference ref=itemDatabaseReference;
            final DatabaseReference gref=listDatabaseReference;

            HashMap<String,Object> values=new HashMap<String, Object>();
            values.put(BotDataModel.S_name,name);
            values.put(BotDataModel.S_desc,about);
            values.put(BotDataModel.S_endpoint,url);
            values.put(BotDataModel.S_secret,secret);
            values.put(BotDataModel.S_dev_no,ph_no);
            values.put(BotDataModel.S_dev_name,dev_name);
            long timestamp=utils.getCurrentTimestamp();
            values.put(BotDataModel.S_timestamp, timestamp);
            values.put(BotDataModel.S_id,key);
            values.put(BotDataModel.S_g_id,g_key);

            BotProviderHelper.AddBot(getApplicationContext(),key,g_key,name,about,url,secret,pic,timestamp,false);


            gref.updateChildren(values);
            ref.updateChildren(values, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError==null){
                        databaseReference.getRef();
                        StorageReference storageRef = mStorage.getReferenceFromUrl("gs://chit-chat-2e791.appspot.com");
                        final StorageReference ProfilePicRef = storageRef.child(ph_no+"/botItem/"+databaseReference.getRef().getKey().substring(1)+"/botpic.png");

                        UploadTask uploadTask = ProfilePicRef.putBytes(pic);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                HashMap<String,Object> values=new HashMap<String, Object>();
                                long pic_timestamp=utils.getCurrentTimestamp();
                                values.put(BotDataModel.S_image_last_update_timestamp,pic_timestamp);
                                values.put(BotDataModel.S_image_url,ProfilePicRef.getPath().toString());
                                BotProviderHelper.update_Image_Timestamp_Updated(getApplicationContext(),key,ProfilePicRef.getPath().toString(),pic_timestamp,true);
                                ref.updateChildren(values);
                                gref.updateChildren(values);
                            }
                        });

                    }
                }
            });


        }
    }
}
