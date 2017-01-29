package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData.customBinder;

public class AppCompatChatListenerActivity extends AppCompatActivity implements ServiceConnection {

	boolean mServiceConnected = false;
	private String mCUrrentId;
	private boolean mis_bot;
	private customBinder mBinder;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		mCUrrentId = null;
		mis_bot = false;
		if (extras != null) {
			restoreValuesFromBundle(extras);
		} else if (savedInstanceState != null) {
			restoreValuesFromBundle(savedInstanceState);

		}
	}

	private void restoreValuesFromBundle(Bundle bundle) {

		if (bundle.getString("type") != null && bundle.getString("type").equals("bot_data_model")) {
			FirebaseBotsDataModel botData = bundle.getParcelable("data");
			mCUrrentId = botData.getGid();
			mis_bot = true;
		} else if (bundle.getString("type") != null && bundle.getString("type").equals("contact_data_model")) {
			ContactItemDataModel contactData = bundle.getParcelable("data");
			mCUrrentId = contactData.getContact_id();
			mis_bot = contactData.is_bot();
		} else if (bundle.getString("type") != null && bundle.getString("type").equals("savedData")) {
			mCUrrentId = bundle.getString("id");
			mis_bot = bundle.getBoolean("is_bot");
		}
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("type", "savedData");
		outState.putString("id", mCUrrentId);
		outState.putBoolean("is_bot", mis_bot);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent chatFetchIntentService = new Intent(this, FetchNewChatData.class);
		bindService(chatFetchIntentService, this, BIND_ABOVE_CLIENT | BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		if (mServiceConnected) {
			unbindService(this);
		}

		super.onStop();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mServiceConnected = true;
		mBinder = (FetchNewChatData.customBinder) service;
		mBinder.getService().setCurrentItem(mCUrrentId, mis_bot);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mServiceConnected = false;
	}

	public void setmCUrrentItem(String id, boolean is_bot) {
		mCUrrentId = id;
		mis_bot = is_bot;
		if (mServiceConnected && mBinder != null) {
			mBinder.getService().setCurrentItem(id, is_bot);
		}
	}
}
