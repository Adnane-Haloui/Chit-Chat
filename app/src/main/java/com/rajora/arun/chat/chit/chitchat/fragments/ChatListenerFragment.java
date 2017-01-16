package com.rajora.arun.chat.chit.chitchat.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;

import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData.customBinder;

import static android.content.Context.BIND_ABOVE_CLIENT;
import static android.content.Context.BIND_AUTO_CREATE;

public class ChatListenerFragment extends Fragment implements ServiceConnection {

    private boolean mServiceConnected;

	@Override
	public void onStart() {
		super.onStart();
		Intent chatFetchIntentService=new Intent(getContext(), FetchNewChatData.class);
		getContext().bindService(chatFetchIntentService,this,BIND_ABOVE_CLIENT|BIND_AUTO_CREATE);
	}

	@Override
    public void onStop() {
		if(mServiceConnected){
			getContext().unbindService(this);
		}
		super.onStop();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mServiceConnected=true;
	    ((customBinder) service).getService().setCurrentItem(null,false);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceConnected=false;
    }

}
