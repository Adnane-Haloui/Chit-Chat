package com.rajora.arun.chat.chit.chitchat.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.rajora.arun.chat.chit.authenticator.login.Login;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.fcm.MyFirebaseInstanceIDService;
import com.rajora.arun.chat.chit.chitchat.fragments.fragment_chat_lists;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGIN=100;
    private static final int REQUEST_CODE_PROFILE=200;
	private static final int REQUEST_PLAY_SERVICES_RESOLUTION = 9000;

    final static String FRAGMENT_TAG_CHAT_LIST="chat_list_fragment_tag";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		checkPlayServices();
        Stetho.initializeWithDefaults(this);

	    sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        if(sharedPreferences.contains("login_status") && sharedPreferences.getString("login_status","").equals("complete")) {
            if(sharedPreferences.contains("first_profile_edit") && sharedPreferences.getBoolean("first_profile_edit",false)){
                showChatFragment(false);
            }
            else{
                    showProfileEdit();
            }
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
	    checkPlayServices();
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
                showProfileEdit();
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

    private void showProfileEdit(){
        Intent intent=new Intent(MainActivity.this,ProfileEditActivity.class);
        startActivityForResult(intent,REQUEST_CODE_PROFILE);
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

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, REQUEST_PLAY_SERVICES_RESOLUTION)
                        .show();
            } else {
	            new AlertDialog.Builder(this).setMessage("Google Play Services Error")
			            .setTitle("This device is not supported for required Goole Play Services")
			            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				            @Override
				            public void onClick(DialogInterface dialogInterface, int i) {
					            finish();
				            }
			            })
			            .create()
			            .show();
            }
            return false;
        }
        return true;
    }

}