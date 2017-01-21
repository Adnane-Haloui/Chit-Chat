package com.rajora.arun.chat.chit.chitchat.dataModels;

/**
 * Created by arc on 4/1/17.
 */

public class FirebaseChatItemDataModel {
	public String content;
	public long g_timestamp;
	public String id;
	public boolean is_bot;
	public String receiver;
	public String sender;
	public long timestamp;
	public String type;

	public FirebaseChatItemDataModel() {
	}

	public FirebaseChatItemDataModel(String content, long g_timestamp, String id, boolean is_bot, String receiver, String sender, long timestamp, String type) {
		this.content = content;
		this.g_timestamp = g_timestamp;
		this.id = id;
		this.is_bot = is_bot;
		this.receiver = receiver;
		this.sender = sender;
		this.timestamp = timestamp;
		this.type = type;
	}
}
