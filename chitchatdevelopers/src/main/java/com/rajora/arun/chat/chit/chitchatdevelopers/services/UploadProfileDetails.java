package com.rajora.arun.chat.chit.chitchatdevelopers.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rajora.arun.chat.chit.chitchatdevelopers.utils.ImageUtils;

import java.util.HashMap;

public class UploadProfileDetails extends IntentService {

    private static final String PARAM_PIC = "PROFILE";
    private static final String PARAM_NAME = "NAME";
    private static final String PARAM_ABOUT = "ABOUT";
    private static final String PARAM_NUMBER = "PHONE_NUMBER";


    public UploadProfileDetails() {
        super("UploadProfileDetails");
    }

    public static void startUploadProfile(Context context, String ph_no, byte pic[], String name, String about) {
        Intent intent = new Intent(context, UploadProfileDetails.class);
        intent.putExtra(PARAM_PIC, pic);
        intent.putExtra(PARAM_NAME, name);
        intent.putExtra(PARAM_ABOUT,about);
        intent.putExtra(PARAM_NUMBER,ph_no);
        context.startService(intent);
    }
    private void NotifyError(){
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("failed","1");
        editor.commit();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final byte[] pic = intent.getByteArrayExtra(PARAM_PIC);
            final String name = intent.getStringExtra(PARAM_NAME);
            final String about = intent.getStringExtra(PARAM_ABOUT);
            final String ph_no=intent.getStringExtra(PARAM_NUMBER);
            SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("name",name);
            editor.putString("about",about);
            if(pic!=null && pic.length!=0)
                editor.putString("pic", ImageUtils.bitmapToString(BitmapFactory.decodeByteArray(pic, 0, pic.length)));
            editor.commit();
            FirebaseDatabase mFirebaseDatabase;
            mFirebaseDatabase=FirebaseDatabase.getInstance();
            final FirebaseStorage mStorage = FirebaseStorage.getInstance();

            HashMap<String,Object> values=new HashMap<String, Object>();
            values.put("name",name);
            values.put("about",about);
            DatabaseReference databaseReference=mFirebaseDatabase.getReference("bots/users/"+ph_no);
            databaseReference.updateChildren(values, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError==null){
                        StorageReference storageRef = mStorage.getReferenceFromUrl("gs://chit-chat-2e791.appspot.com");
                        StorageReference ProfilePicRef = storageRef.child(ph_no+"/bots/profile/profilepic.png");

                        UploadTask uploadTask = ProfilePicRef.putBytes(pic);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                NotifyError();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        });

                    }
                    else{
                        NotifyError();
                    }
                }
            });


        }
    }
}
