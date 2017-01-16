package com.rajora.arun.chat.chit.chitchat.dataModels;

/**
 * Created by arc on 4/1/17.
 */

public class FirebaseChatItemDataModel {
	private String content;
	private long g_timestamp;
	private String id;
	private boolean is_bot;
	private String receiver;
	private String sender;
	private long timestamp;
	private String type;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getG_timestamp() {
		return g_timestamp;
	}

	public void setG_timestamp(long g_timestamp) {
		this.g_timestamp = g_timestamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean is_bot() {
		return is_bot;
	}

	public void setIs_bot(boolean is_bot) {
		this.is_bot = is_bot;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
