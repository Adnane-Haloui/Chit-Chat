package com.rajora.arun.chat.chit.chitchat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.rajora.arun.chat.chit.authenticator.login.Login;
import com.rajora.arun.chat.chit.authenticator.login.User_Metadata;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.dataBase.Helper.chat_database;
import com.rajora.arun.chat.chit.chitchat.fcm.MyFirebaseInstanceIDService;
import com.rajora.arun.chat.chit.chitchat.fragments.fragment_chat_lists;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final static int REQUEST_CODE_LOGIN=100;
    final static int REQUEST_CODE_PROFILE=200;

    final static String FRAGMENT_TAG_CHAT_LIST="chat_list_fragment_tag";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        if(sharedPreferences.contains("login_status") && sharedPreferences.getString("login_status","").equals("complete")) {
            showChatFragment(false);
        }
        else{
            startFirebaseForAuthentication();
        }
        if(sharedPreferences.contains("updateFirebaseInstanceIdOnAuth") &&
                sharedPreferences.getBoolean("updateFirebaseInstanceIdOnAuth",false)){
            MyFirebaseInstanceIDService.updateTokens(sharedPreferences);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(canShowChatFragment()) {
            showChatFragment(false);
        }
    }

    private void startFirebaseForAuthentication(){

        final Intent intent=new Intent(this,Login.class);
        startActivityForResult(intent,REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_CODE_LOGIN && resultCode== Activity.RESULT_OK){
            if(sharedPreferences.contains("login_status") && sharedPreferences.getString("login_status","").equals("complete"))
            {
                if(sharedPreferences.contains("updateFirebaseInstanceIdOnAuth") &&
                        sharedPreferences.getBoolean("updateFirebaseInstanceIdOnAuth",false)){
                    MyFirebaseInstanceIDService.updateTokens(sharedPreferences);
                }
                Intent intent=new Intent(MainActivity.this,ProfileEditActivity.class);
                startActivityForResult(intent,REQUEST_CODE_PROFILE);
            }
            else{
                finish();
            }
        }
        else if(requestCode==REQUEST_CODE_PROFILE){
            if(canShowChatFragment()) {
                showChatFragment(true);
            }
        }
    }

    private void showChatFragment(boolean commitAllowingStateLoss){
        if(commitAllowingStateLoss){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_chat_list_holder,new fragment_chat_lists(),FRAGMENT_TAG_CHAT_LIST).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_chat_list_holder,new fragment_chat_lists(),FRAGMENT_TAG_CHAT_LIST).commitAllowingStateLoss();
        }
    }

    private boolean canShowChatFragment(){
        return sharedPreferences.contains("login_status") && sharedPreferences.getString("login_status","").equals("complete")
                && getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_CHAT_LIST)==null;
    }
}