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


public class AboutActivity extends AppCompatChatListenerActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

}
