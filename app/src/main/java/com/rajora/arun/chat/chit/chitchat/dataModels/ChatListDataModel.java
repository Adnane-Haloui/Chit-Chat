package com.rajora.arun.chat.chit.chitchat.dataModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChatList;

/**
 * Created by arc on 24/12/16.
 */

public class ChatListDataModel implements Parcelable {

	public long id;
    public String contact_id;
    public boolean is_bot;
	public String name;
	public String pic_uri;
	public String pic_url;
	public int unread_count;
	public String last_message;
	public String last_message_type;
	public long last_message_time;

	public ChatListDataModel() {
	}

	public ChatListDataModel(long id, String contact_id, boolean is_bot, String name, String pic_uri, String pic_url, int unread_count, String last_message, String last_message_type, long last_message_time) {
		this.id = id;
		this.contact_id = contact_id;
		this.is_bot = is_bot;
		this.name = name;
		this.pic_uri = pic_uri;
		this.pic_url = pic_url;
		this.unread_count = unread_count;
		this.last_message = last_message;
		this.last_message_type = last_message_type;
		this.last_message_time = last_message_time;
	}

	public ChatListDataModel(Cursor cursor){
		if(cursor!=null){
			if(cursor.getColumnIndex(ContractChatList._ID)>=0){
				id=cursor.getLong(cursor.getColumnIndex(ContractChatList._ID));
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_CONTACT_ID)>=0){
				contact_id=cursor.getString(cursor.getColumnIndex(ContractChatList.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_IS_BOT)>=0){
				is_bot=cursor.getInt(cursor.getColumnIndex(ContractChatList.COLUMN_IS_BOT))!=0;
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_NAME)>=0){
				name=cursor.getString(cursor.getColumnIndex(ContractChatList.COLUMN_NAME));
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_PIC_URI)>=0){
				pic_uri=cursor.getString(cursor.getColumnIndex(ContractChatList.COLUMN_PIC_URI));
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_PIC_URL)>=0){
				pic_url=cursor.getString(cursor.getColumnIndex(ContractChatList.COLUMN_PIC_URL));
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_UNREAD_COUNT)>=0){
				if(cursor.isNull(cursor.getColumnIndex(ContractChatList.COLUMN_UNREAD_COUNT))){
					unread_count=cursor.getInt(cursor.getColumnIndex(ContractChatList.COLUMN_UNREAD_COUNT));
				}
				else{
					unread_count=0;
				}
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_LAST_MESSAGE)>=0){
				last_message=cursor.getString(cursor.getColumnIndex(ContractChatList.COLUMN_LAST_MESSAGE));
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_LAST_MESSAGE_TYPE)>=0){
				last_message_type=cursor.getString(cursor.getColumnIndex(ContractChatList.COLUMN_LAST_MESSAGE_TYPE));
			}
			if(cursor.getColumnIndex(ContractChatList.COLUMN_LAST_MESSAGE_TIMESTAMP)>=0){
				last_message_time=cursor.getLong(cursor.getColumnIndex(ContractChatList.COLUMN_LAST_MESSAGE_TIMESTAMP));
			}
		}
	}

	public ContactItemDataModel getContactItemDataModel(){
		return new ContactItemDataModel(contact_id,is_bot);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.contact_id);
		dest.writeByte(this.is_bot ? (byte) 1 : (byte) 0);
		dest.writeString(this.name);
		dest.writeString(this.pic_uri);
		dest.writeString(this.pic_url);
		dest.writeInt(this.unread_count);
		dest.writeString(this.last_message);
		dest.writeString(this.last_message_type);
		dest.writeLong(this.last_message_time);
	}

	protected ChatListDataModel(Parcel in) {
		this.id = in.readLong();
		this.contact_id = in.readString();
		this.is_bot = in.readByte() != 0;
		this.name = in.readString();
		this.pic_uri = in.readString();
		this.pic_url = in.readString();
		this.unread_count = in.readInt();
		this.last_message = in.readString();
		this.last_message_type = in.readString();
		this.last_message_time = in.readLong();
	}

	public static final Parcelable.Creator<ChatListDataModel> CREATOR = new Parcelable.Creator<ChatListDataModel>() {
		@Override
		public ChatListDataModel createFromParcel(Parcel source) {
			return new ChatListDataModel(source);
		}

		@Override
		public ChatListDataModel[] newArray(int size) {
			return new ChatListDataModel[size];
		}
	};
}
