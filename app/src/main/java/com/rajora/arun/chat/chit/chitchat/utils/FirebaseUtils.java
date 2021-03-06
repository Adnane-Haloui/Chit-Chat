package com.rajora.arun.chat.chit.chitchat.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;

import java.util.HashMap;

public class FirebaseUtils {

	public static DatabaseReference getReferenceForUser(FirebaseDatabase firebaseDatabase, String ph_no) {
		return firebaseDatabase.getReference("users/" + ph_no.substring(1) + "/");
	}

	public static String sendChatMessageToFirebase(FirebaseDatabase firebaseDatabase, ChatItemDataModel item, String ph_no) {
		DatabaseReference itemDatabaseReference;
		if (item.is_bot) {
			itemDatabaseReference = firebaseDatabase.getReference("botChatItems/" + item.contact_id + "/");
		} else {
			itemDatabaseReference = firebaseDatabase.getReference("chatItems/" +
					item.contact_id.substring(1) + "/");
		}
		DatabaseReference revItemReference = firebaseDatabase.getReference("chatItems/" +
				ph_no.substring(1) + "/");
		final String id_on_server = itemDatabaseReference.push().getKey().substring(1);
		itemDatabaseReference = itemDatabaseReference.child(id_on_server);
		final String cur_id_on_server = revItemReference.push().getKey().substring(1);
		HashMap<String, Object> fbvalues = new HashMap<>();
		fbvalues.put("sender", ph_no);
		fbvalues.put("receiver", item.contact_id);
		fbvalues.put("is_bot", item.is_bot);
		fbvalues.put("type", item.message_type);
		fbvalues.put("content", item.message);
		fbvalues.put("id", id_on_server);
		fbvalues.put("timestamp", item.timestamp);
		fbvalues.put("g_timestamp", ServerValue.TIMESTAMP);

		revItemReference.child("-" + cur_id_on_server).updateChildren(fbvalues);
		itemDatabaseReference.updateChildren(fbvalues);
		return cur_id_on_server;
	}

	public static String getUniqueChatIdForFile(FirebaseDatabase firebaseDatabase, ChatItemDataModel item, String from_id) {
		DatabaseReference itemDatabaseReference = firebaseDatabase.getReference("chatItems/" +
				from_id.substring(1) + "/");
		return itemDatabaseReference.push().getKey().substring(1);
	}

	public static void sendFileTextMessageToFirebase(FirebaseDatabase firebaseDatabase, ChatItemDataModel item, String from_id) {
		DatabaseReference itemDatabaseReference;
		if (item.is_bot) {
			itemDatabaseReference = firebaseDatabase.getReference("botChatItems/" + item.contact_id + "/");
		} else {
			itemDatabaseReference = firebaseDatabase.getReference("chatItems/" +
					item.contact_id.substring(1) + "/");
		}
		DatabaseReference revItemReference = firebaseDatabase.getReference("chatItems/" +
				from_id.substring(1) + "/").child("-" + item.chat_id);
		final String id_on_server = itemDatabaseReference.push().getKey().substring(1);
		itemDatabaseReference = itemDatabaseReference.child(id_on_server);

		HashMap<String, Object> fbvalues = new HashMap<>();
		fbvalues.put("sender", from_id);
		fbvalues.put("receiver", item.contact_id);
		fbvalues.put("is_bot", item.is_bot);
		fbvalues.put("type", item.message_type);
		fbvalues.put("content", item.message);
		fbvalues.put("id", id_on_server);
		fbvalues.put("timestamp", item.timestamp);
		fbvalues.put("g_timestamp", ServerValue.TIMESTAMP);

		revItemReference.updateChildren(fbvalues);
		itemDatabaseReference.updateChildren(fbvalues);
	}
}
