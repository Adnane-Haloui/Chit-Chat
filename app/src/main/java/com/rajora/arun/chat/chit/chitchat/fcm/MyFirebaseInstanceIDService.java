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
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String oldToken=null;
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        String ph_no=sharedPreferences.getString("phone",null);

        if(ph_no!=null){
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("fcmTokens/"+ph_no+"/");

            if(sharedPreferences.contains("fcm-token")){
                oldToken=sharedPreferences.getString("fcm-token",null);
            }
            if(oldToken==null){
                final String id_on_server=ref.push().getKey().substring(1);
                DatabaseReference reference=ref.child(id_on_server);
                reference.setValue(refreshedToken);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("id-for-fcm",id_on_server);
                editor.commit();
            }
            else{
                final String id_on_server=sharedPreferences.getString("id-for-fcm",null);
                if(id_on_server!=null){
                    ref=ref.child(id_on_server);
                    ref.setValue(refreshedToken);
                }
            }
        }
    SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("fcm-token",refreshedToken);
        edit.commit();

    }
}
