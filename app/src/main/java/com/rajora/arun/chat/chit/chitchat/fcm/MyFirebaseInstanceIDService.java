package com.rajora.arun.chat.chit.chitchat.fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by arc on 27/10/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        updateTokens(sharedPreferences);
    }
    public static void updateTokens(SharedPreferences sharedPreferences){
        SharedPreferences.Editor edit=sharedPreferences.edit();

        String oldToken=null;
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String ph_no=sharedPreferences.getString("phone",null);

        if(ph_no!=null && sharedPreferences.contains("login_status") &&
                sharedPreferences.getString("login_status","").equals("complete")){
            ph_no=ph_no.substring(1);
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("fcmTokens/"+ph_no+"/");

            if(sharedPreferences.contains("fcm-token")){
                oldToken=sharedPreferences.getString("fcm-token",null);
            }
            if(oldToken==null){
                pushNewToken(refreshedToken,ref,sharedPreferences);
            }
            else{
                updateOldToken(refreshedToken,ref,sharedPreferences);
            }
        }
        else{
            edit.putBoolean("updateFirebaseInstanceIdOnAuth",true);
        }
        edit.putString("fcm-token",refreshedToken);
        edit.commit();
    }
    private static void pushNewToken(String token,DatabaseReference ref,SharedPreferences sharedPreferences){
        final String id_on_server=ref.push().getKey().substring(1);
        DatabaseReference reference=ref.child(id_on_server);
        reference.setValue(token);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("id-for-fcm",id_on_server);
        editor.putBoolean("updateFirebaseInstanceIdOnAuth",false);
        editor.commit();

    }
    private static void updateOldToken(String token,DatabaseReference ref,SharedPreferences sharedPreferences){
        final String id_on_server=sharedPreferences.getString("id-for-fcm",null);
        if(id_on_server!=null){
            ref=ref.child(id_on_server);
            ref.setValue(token);
        }
        else{
            pushNewToken(token,ref,sharedPreferences);
        }
    }
}
