package com.rajora.arun.chat.chit.chitchat.dataModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractNotificationList;

/**
 * Created by arc on 4/1/17.
 */

public class NotificationDataModel implements Parcelable {
	public String contact_id;
	public boolean is_bot;
	public String name;
	public String pic_url;
	public String pic_uri;
	public String message;
	public String message_type;
	public long message_timestamp;

	public NotificationDataModel() {
	}

	public NotificationDataModel(String contact_id, boolean is_bot, String name, String pic_url, String pic_uri, String message, String message_type, long message_timestamp) {
		this.contact_id = contact_id;
		this.is_bot = is_bot;
		this.name = name;
		this.pic_url = pic_url;
		this.pic_uri = pic_uri;
		this.message = message;
		this.message_type = message_type;
		this.message_timestamp = message_timestamp;
	}

	public NotificationDataModel(Cursor cursor){
		if(cursor!=null){
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID)>=0){
				contact_id=cursor.getString(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_IS_BOT)>=0){
				is_bot=cursor.getInt(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID))!=0;
			}
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_NAME)>=0){
				name=cursor.getString(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_PIC_URI)>=0){
				pic_uri=cursor.getString(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_PIC_URL)>=0){
				pic_url=cursor.getString(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_MESSAGE)>=0){
				message=cursor.getString(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_MESSAGE_TYPE)>=0){
				message_type=cursor.getString(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractNotificationList.COLUMN_MESSAGE_TIMESTAMP)>=0){
				message_timestamp=cursor.getLong(cursor.getColumnIndex(ContractNotificationList.COLUMN_CONTACT_ID));
			}
		}
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.contact_id);
		dest.writeByte(this.is_bot ? (byte) 1 : (byte) 0);
		dest.writeString(this.name);
		dest.writeString(this.pic_url);
		dest.writeString(this.pic_uri);
		dest.writeString(this.message);
		dest.writeString(this.message_type);
		dest.writeLong(this.message_timestamp);
	}

	protected NotificationDataModel(Parcel in) {
		this.contact_id = in.readString();
		this.is_bot = in.readByte() != 0;
		this.name = in.readString();
		this.pic_url = in.readString();
		this.pic_uri = in.readString();
		this.message = in.readString();
		this.message_type = in.readString();
		this.message_timestamp = in.readLong();
	}

	public static final Parcelable.Creator<NotificationDataModel> CREATOR = new Parcelable.Creator<NotificationDataModel>() {
		@Override
		public NotificationDataModel createFromParcel(Parcel source) {
			return new NotificationDataModel(source);
		}

		@Override
		public NotificationDataModel[] newArray(int size) {
			return new NotificationDataModel[size];
		}
	};
}
