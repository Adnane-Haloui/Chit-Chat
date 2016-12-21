package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.SimpleJobService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class UploadProfileDetails extends SimpleJobService {

    public static final String PARAM_PIC = "PROFILE";
    public static final String PARAM_NAME = "NAME";
    public static final String PARAM_ABOUT = "ABOUT";

    @Override
    public int onRunJob(JobParameters job) {
        return UploadProfileDetails(job)?RESULT_SUCCESS:RESULT_FAIL_NORETRY;
    }

    private boolean UploadProfileDetails(JobParameters job){
        Bundle extras = job.getExtras();
        if(extras!=null){

            final String picString = extras.getString(PARAM_PIC);
            final String name = extras.getString(PARAM_NAME);
            final String about = extras.getString(PARAM_ABOUT);

            byte[] mpic=null;
            if(picString!=null && picString.length()>0){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageUtils.stringToBitmap(picString).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                mpic=baos.toByteArray();
            }
            final byte[] pic=mpic;

            SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
            FirebaseDatabase mFirebaseDatabase=FirebaseDatabase.getInstance();
            final FirebaseStorage mStorage = FirebaseStorage.getInstance();

            final String ph_no=sharedPreferences.getString("phone","bad-data").substring(1);

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("name",name);
            editor.putString("about",about);
            if(pic!=null && pic.length!=0)
                editor.putString("pic", ImageUtils.bitmapToString(BitmapFactory.decodeByteArray(pic, 0, pic.length)));
            editor.commit();


            HashMap<String,Object> values=new HashMap<String, Object>();
            values.put("name",name);
            values.put("about",about);
            DatabaseReference databaseReference=mFirebaseDatabase.getReference("users/"+ph_no);
            databaseReference.updateChildren(values, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                    if(databaseError==null){
                        StorageReference storageRef = mStorage.getReferenceFromUrl("gs://chit-chat-2e791.appspot.com");
                        StorageReference ProfilePicRef = storageRef.child(ph_no+"/profile/profilepic.png");
                        if(pic!=null){
                            UploadTask uploadTask = ProfilePicRef.putBytes(pic);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e("errors",exception.getMessage());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    HashMap<String,Object> picValues=new HashMap<String, Object>();
                                    picValues.put("profilePic_timestamp", utils.getCurrentTimestamp());
                                    databaseReference.updateChildren(picValues);
                                }
                            });
                        }
                    }
                    else{
                        Log.e("errors",databaseError.getMessage());
                    }
                }
            });
        }
        return true;
    }

}
