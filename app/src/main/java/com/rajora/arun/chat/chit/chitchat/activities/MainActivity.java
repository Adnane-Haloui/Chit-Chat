package com.rajora.arun.chat.chit.chitchat.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rajora.arun.chat.chit.authenticator.login.Login;
import com.rajora.arun.chat.chit.authenticator.login.User_Metadata;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.dataBase.Helper.chat_database;
import com.rajora.arun.chat.chit.chitchat.fragments.fragment_chat_lists;

public class MainActivity extends AppCompatActivity {

    final static int REQUEST_CODE_LOGIN=100;
    final static int REQUEST_CODE_PROFILE=200;

    final static String FRAGMENT_TAG_CHAT_LIST="chat_list_fragment_tag";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        if(sharedPreferences.contains("name"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_chat_list_holder,new fragment_chat_lists(),FRAGMENT_TAG_CHAT_LIST).commit();
        }
        else if(sharedPreferences.contains("firebase_auth_token")){
            Intent intent=new Intent(MainActivity.this,ProfileEditActivity.class);
            startActivity(intent);
        }
        else{
            startFirebaseForAuthentication();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        if(sharedPreferences.contains("name") && getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_CHAT_LIST)==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_chat_list_holder,new fragment_chat_lists(),FRAGMENT_TAG_CHAT_LIST).commit();
        }
    }

    private void startFirebaseForAuthentication(){

        Intent intent=new Intent(this,Login.class);
        mAuth=FirebaseAuth.getInstance();
        startActivityForResult(intent,REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_CODE_LOGIN && resultCode== Activity.RESULT_OK){
            Toast.makeText(this,data.getStringExtra(User_Metadata.PHONE_NUMBER),Toast.LENGTH_SHORT).show();
            String ph_no=data.getStringExtra(User_Metadata.PHONE_NUMBER);
            String token=data.getStringExtra(User_Metadata.O_AUTH_TOKEN);
            String secret=data.getStringExtra(User_Metadata.O_AUTH_SECRET);
            String firebase_token=data.getStringExtra(User_Metadata.FIREBASE_TOKEN);
            Log.d("findme","phone "+ph_no);
            Log.d("findme","token "+token);
            Log.d("findme","secret "+secret);
            Log.d("findme","firebase_token "+firebase_token);
            final SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("firebase_auth_token",firebase_token);
            editor.putString("phone", ph_no);
            editor.commit();
            mAuth.signInWithCustomToken(firebase_token)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(MainActivity.this,"ready",Toast.LENGTH_SHORT).show();
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Auth failed on firebase",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                    Intent intent=new Intent(MainActivity.this,ProfileEditActivity.class);
                                    startActivityForResult(intent,REQUEST_CODE_PROFILE);
                            }
                        }
                    });
        }
        else if(requestCode==REQUEST_CODE_PROFILE){
            final SharedPreferences sPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
            if(sPreferences.contains("name") && getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_CHAT_LIST)==null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_chat_list_holder,new fragment_chat_lists(),FRAGMENT_TAG_CHAT_LIST).commitAllowingStateLoss();
            }
            else{
                sPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if(sharedPreferences.contains("name") && getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_CHAT_LIST)==null)
                        {
                            sPreferences.unregisterOnSharedPreferenceChangeListener(this);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_chat_list_holder,new fragment_chat_lists(),FRAGMENT_TAG_CHAT_LIST).commitAllowingStateLoss();

                        }
                    }
                });

            }
        }
    }
}
