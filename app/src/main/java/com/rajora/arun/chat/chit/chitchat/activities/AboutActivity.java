package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;


public class AboutActivity extends AppCompatActivity {


    FetchNewChatData mBoundService;
    boolean service_connected=false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service_connected=false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            service_connected=true;
            FetchNewChatData.customBinder myBinder = (FetchNewChatData.customBinder) service;
            mBoundService = myBinder.getService();
            mBoundService.setCurrentItemId(null);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, FetchNewChatData.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        if(service_connected )
            unbindService(mServiceConnection);
        super.onPause();
    }
}
