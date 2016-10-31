package com.rajora.arun.chat.chit.chitchat.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewDataOnce;

/**
 * Created by arc on 27/10/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        FetchNewDataOnce.startChatFtech(this);
    }
}
