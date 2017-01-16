package com.rajora.arun.chat.chit.chitchat.dataModels;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;

public class ChatItemDataModel implements Parcelable {

    public long id;
	public String chat_id;
    public String contact_id;
    public boolean is_bot;
    public String message;
    public String message_direction;
    public String message_status;
    public String message_type;
    public long timestamp;
    public String extra_uri;
    public String upload_status;

	public ChatItemDataModel() {
	}

	public ChatItemDataModel(String contact_id, boolean is_bot, String message, String message_direction, String message_status, String message_type, long timestamp) {
		this.contact_id = contact_id;
		this.is_bot = is_bot;
		this.message = message;
		this.message_direction = message_direction;
		this.message_status = message_status;
		this.message_type = message_type;
		this.timestamp = timestamp;
	}

	public ChatItemDataModel(long id, String chat_id, String contact_id, boolean is_bot, String message, String message_direction, String message_status, String message_type, long timestamp, String extra_uri, String upload_status) {
		this.id = id;
		this.chat_id = chat_id;
		this.contact_id = contact_id;
		this.is_bot = is_bot;
		this.message = message;
		this.message_direction = message_direction;
		this.message_status = message_status;
		this.message_type = message_type;
		this.timestamp = timestamp;
		this.extra_uri = extra_uri;
		this.upload_status = upload_status;
	}

	public ChatItemDataModel(Cursor cursor){
		if(cursor!=null){
			if(cursor.getColumnIndex(ContractChat._ID)>=0){
				id=cursor.getLong(cursor.getColumnIndex(ContractChat._ID));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_CHAT_ID)>=0){
				chat_id=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_CHAT_ID));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_CONTACT_ID)>=0){
				contact_id=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_IS_BOT)>=0){
				is_bot=cursor.getInt(cursor.getColumnIndex(ContractChat.COLUMN_IS_BOT))!=0;
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_DIRECTION)>=0){
				message_direction=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_DIRECTION));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_TIMESTAMP)>=0){
				timestamp=cursor.getLong(cursor.getColumnIndex(ContractChat.COLUMN_TIMESTAMP));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE)>=0){
				message=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_TYPE)>=0){
				message_type=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_TYPE));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_STATUS)>=0){
				message_status=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_STATUS));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_UPLOAD_STATUS)>=0){
				upload_status=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_UPLOAD_STATUS));
			}
			if(cursor.getColumnIndex(ContractChat.COLUMN_EXTRA_URI)>=0){
				extra_uri=cursor.getString(cursor.getColumnIndex(ContractChat.COLUMN_EXTRA_URI));
			}
		}
	}

	public ContentValues getMessageContentValues(){
		ContentValues contentValues=new ContentValues();
		contentValues.put(ContractChat.COLUMN_CHAT_ID,chat_id);
		contentValues.put(ContractChat.COLUMN_CONTACT_ID,contact_id);
		contentValues.put(ContractChat.COLUMN_IS_BOT,is_bot);
		contentValues.put(ContractChat.COLUMN_MESSAGE,message);
		contentValues.put(ContractChat.COLUMN_MESSAGE_DIRECTION,message_direction);
		contentValues.put(ContractChat.COLUMN_MESSAGE_STATUS,message_status);
		contentValues.put(ContractChat.COLUMN_MESSAGE_TYPE,message_type);
		contentValues.put(ContractChat.COLUMN_TIMESTAMP,timestamp);
		return contentValues;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.chat_id);
		dest.writeString(this.contact_id);
		dest.writeByte(this.is_bot ? (byte) 1 : (byte) 0);
		dest.writeString(this.message);
		dest.writeString(this.message_direction);
		dest.writeString(this.message_status);
		dest.writeString(this.message_type);
		dest.writeLong(this.timestamp);
		dest.writeString(this.extra_uri);
		dest.writeString(this.upload_status);
	}

	protected ChatItemDataModel(Parcel in) {
		this.id = in.readLong();
		this.chat_id = in.readString();
		this.contact_id = in.readString();
		this.is_bot = in.readByte() != 0;
		this.message = in.readString();
		this.message_direction = in.readString();
		this.message_status = in.readString();
		this.message_type = in.readString();
		this.timestamp = in.readLong();
		this.extra_uri = in.readString();
		this.upload_status = in.readString();
	}

	public static final Parcelable.Creator<ChatItemDataModel> CREATOR = new Parcelable.Creator<ChatItemDataModel>() {
		@Override
		public ChatItemDataModel createFromParcel(Parcel source) {
			return new ChatItemDataModel(source);
		}

		@Override
		public ChatItemDataModel[] newArray(int size) {
			return new ChatItemDataModel[size];
		}
	};
}
