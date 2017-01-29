package com.rajora.arun.chat.chit.chitchat.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ChatListRemoteViewService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new RemoteViewFactory(getApplicationContext());
	}
}
