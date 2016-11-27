package com.rajora.arun.chat.chit.chitchat.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.digits.sdk.android.models.PhoneNumber;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

public class updateContactsDbFromPhoneDb extends IntentService {

    public updateContactsDbFromPhoneDb() {
        super("updateContactsDbFromPhoneDb");
    }

    public static void startContactDbUpdate(Context context) {
        Intent intent = new Intent(context, updateContactsDbFromPhoneDb.class);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();

        final Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]{
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY},
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER+" IS NOT NULL ",null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY+" ASC");
        if(cursor!=null && cursor.moveToFirst()){
            do{
                String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String about="";
                String temp_Ph_No=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                PhoneNumberUtil phoneNumberUtil=PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber pn = null;
                try {
                    Phonenumber.PhoneNumber numberProto = phoneNumberUtil.parse(temp_Ph_No, "");
                    temp_Ph_No = phoneNumberUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
                } catch (Exception e) {
                    try {
                        pn = phoneNumberUtil.parse(temp_Ph_No,((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso().toUpperCase());
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    try{
                        temp_Ph_No = phoneNumberUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
                    } catch (Exception ee){
                        ee.printStackTrace();
                    }
                }

                final String ph_no=temp_Ph_No;
                String photo_uri=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                boolean is_user=false;
                Bitmap img=null;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] byteArray;

                Cursor presentValue=getContentResolver().query(ChatContentProvider.CONTACTS_URI,new String[]{
                        contract_contacts.COLUMN_PH_NUMBER,
                        contract_contacts.COLUMN_IS_USER,
                        contract_contacts.COLUMN_PIC},
                        contract_contacts.COLUMN_PH_NUMBER+" = ?",new String[]{ph_no},null);

                if(presentValue!=null &&  presentValue.getCount()>0 && presentValue.moveToFirst()){

                    is_user|=(presentValue.getInt(presentValue.getColumnIndex(contract_contacts.COLUMN_IS_USER))>0);
                    if(presentValue.isNull(presentValue.getColumnIndex(contract_contacts.COLUMN_PIC))){
                        String imageUriString=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        Uri imageUri=null;
                        if(imageUriString!=null)
                        {
                            imageUri=Uri.parse(imageUriString);
                        }
                        if(imageUri!=null){
                            try {
                                img=MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri );
                                img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if(img!=null)
                            byteArray=stream.toByteArray();
                        else
                            byteArray=null;
                        ContentValues values=new ContentValues();
                        values.put(contract_contacts.COLUMN_NAME,name);
                        values.put(contract_contacts.COLUMN_PH_NUMBER,ph_no);
                        values.put(contract_contacts.COLUMN_IS_USER,is_user);
                        values.put(contract_contacts.COLUMN_PIC,byteArray);
                        getContentResolver().update(ChatContentProvider.CONTACTS_URI,values,contract_contacts.COLUMN_PH_NUMBER+" = ?",new String[]{ph_no});

                    }
                }
                else{

                    String imageUriString=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    Uri imageUri=null;
                    if(imageUriString!=null)
                    {
                        imageUri=Uri.parse(imageUriString);
                    }
                    if(imageUri!=null){
                        try {
                            img=MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri );
                            img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(img!=null)
                        byteArray=stream.toByteArray();
                    else
                        byteArray=null;

                    ContentValues values=new ContentValues();
                    values.put(contract_contacts.COLUMN_NAME,name);
                    values.put(contract_contacts.COLUMN_PH_NUMBER,ph_no);
                    values.put(contract_contacts.COLUMN_IS_USER,is_user);
                    values.put(contract_contacts.COLUMN_PIC,byteArray);
                    getContentResolver().insert(ChatContentProvider.CONTACTS_URI,values);
                }

                DatabaseReference ref=firebaseDatabase.getReference("users/"+ph_no.substring(1)+"/");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ContentValues nval=new ContentValues();
                        if(dataSnapshot.child("name").exists()){
                            nval.put(contract_contacts.COLUMN_IS_USER,true);
                        }
                        if(dataSnapshot.child("about").exists()){
                            nval.put(contract_contacts.COLUMN_ABOUT,dataSnapshot.child("about").getValue().toString());
                            nval.put(contract_contacts.COLUMN_IS_USER,true);
                        }
                        if(dataSnapshot.child("profilePic_timestamp").exists()){
                            nval.put(contract_contacts.COLUMN_PIC_TIMESTAMP,dataSnapshot.child("profilePic_timestamp").getValue().toString());
                            nval.put(contract_contacts.COLUMN_IS_USER,true);

                            Cursor db_value_cur=getContentResolver().query(ChatContentProvider.CONTACTS_URI,new String[]{
                                    contract_contacts.COLUMN_PIC_TIMESTAMP,contract_contacts.COLUMN_PIC},
                                    contract_contacts.COLUMN_PH_NUMBER+" = ? ",new String[]{ph_no},null);
                            if(db_value_cur!=null && db_value_cur.getCount()>0 && db_value_cur.moveToFirst()){

                                Long timestampInDb=-1L;
                                if(!db_value_cur.isNull(db_value_cur.getColumnIndex(contract_contacts.COLUMN_PIC_TIMESTAMP)))
                                    timestampInDb=db_value_cur.getLong(db_value_cur.getColumnIndex(contract_contacts.COLUMN_PIC_TIMESTAMP));
                                final Long timestampInFb=Long.parseLong(dataSnapshot.child("profilePic_timestamp").getValue().toString());

                                if(timestampInDb==-1 || timestampInFb>timestampInDb ||
                                        db_value_cur.isNull(db_value_cur.getColumnIndex(contract_contacts.COLUMN_PIC))){
                                    StorageReference reference=firebaseStorage.getReferenceFromUrl("gs://chit-chat-2e791.appspot.com/"+ph_no.substring(1)+"/profile/profilepic.png");
                                    reference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            ContentValues imgUpdVal=new ContentValues();
                                            imgUpdVal.put(contract_contacts.COLUMN_PIC,bytes);
                                            imgUpdVal.put(contract_contacts.COLUMN_PIC_TIMESTAMP,timestampInFb);
                                            if(bytes!=null)
                                                getContentResolver().update(ChatContentProvider.CONTACTS_URI,
                                                        imgUpdVal,contract_contacts.COLUMN_PH_NUMBER+" = ?",new String[]{ph_no});
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                                }
                                db_value_cur.close();

                            }
                        }
                        if(nval.containsKey(contract_contacts.COLUMN_IS_USER)){
                            getContentResolver().update(ChatContentProvider.CONTACTS_URI,nval,contract_contacts.COLUMN_PH_NUMBER+" = ?",new String[]{ph_no});
                        }
                     }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                if(presentValue!=null)
                    presentValue.close();
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

}
