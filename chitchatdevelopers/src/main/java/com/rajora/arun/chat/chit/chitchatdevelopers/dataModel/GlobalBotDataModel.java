package com.rajora.arun.chat.chit.chitchatdevelopers.dataModel;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;


public class GlobalBotDataModel implements Parcelable {

	public String id;
	public String Gid;
	public String name;
	public String desc;
	public String dev_name;
	public String endpoint;
	public String secret;
	public String image_url;
	public long image_last_update_timestamp;
	public long timestamp;
	public boolean is_deleted;

	public GlobalBotDataModel() {
	}

	public GlobalBotDataModel(String id, String gid, String name, String desc, String dev_name, String endpoint, String secret, String image_url, long image_last_update_timestamp, long timestamp, boolean is_deleted) {
		this.id = id;
		Gid = gid;
		this.name = name;
		this.desc = desc;
		this.dev_name = dev_name;
		this.endpoint = endpoint;
		this.secret = secret;
		this.image_url = image_url;
		this.image_last_update_timestamp = image_last_update_timestamp;
		this.timestamp = timestamp;
		this.is_deleted = is_deleted;
	}

	public GlobalBotDataModel(Cursor cursor){
		if(cursor!=null){
			if(cursor.getColumnIndex(BotContracts.COLUMN_ID)>=0){
				id=cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_ID));
			}
			if(cursor.getColumnIndex(BotContracts.COLUMN_GLOBAL_ID)>=0){
				Gid=cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_GLOBAL_ID));
			}
			if(cursor.getColumnIndex(BotContracts.COLUMN_BOT_NAME)>=0){
				name=cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_BOT_NAME));
			}
			if(cursor.getColumnIndex(BotContracts.COLUMN_ABOUT)>=0){
				desc=cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_ABOUT));
			}
			if(cursor.getColumnIndex(BotContracts.COLUMN_API_ENDPOINT)>=0){
				endpoint=cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_API_ENDPOINT));
			}
			if(cursor.getColumnIndex(BotContracts.COLUMN_SECRET)>=0){
				secret=cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_SECRET));
			}

			if(cursor.getColumnIndex(BotContracts.COLUMN_PIC_URI)>=0){
				image_url=cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_PIC_URI));
			}
			if(cursor.getColumnIndex(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP)>=0){
				image_last_update_timestamp=cursor.getLong(cursor.getColumnIndex(BotContracts.COLUMN_IMAGE_UPDATE_TIMESTAMP));
			}
			if(cursor.getColumnIndex(BotContracts.COLUMN_TIMESTAMP)>=0){
				timestamp=cursor.getLong(cursor.getColumnIndex(BotContracts.COLUMN_TIMESTAMP));
			}

			is_deleted=false;
		}
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.Gid);
		dest.writeString(this.name);
		dest.writeString(this.desc);
		dest.writeString(this.dev_name);
		dest.writeString(this.endpoint);
		dest.writeString(this.secret);
		dest.writeString(this.image_url);
		dest.writeLong(this.image_last_update_timestamp);
		dest.writeLong(this.timestamp);
		dest.writeByte(this.is_deleted ? (byte) 1 : (byte) 0);
	}

	protected GlobalBotDataModel(Parcel in) {
		this.id = in.readString();
		this.Gid = in.readString();
		this.name = in.readString();
		this.desc = in.readString();
		this.dev_name = in.readString();
		this.endpoint = in.readString();
		this.secret = in.readString();
		this.image_url = in.readString();
		this.image_last_update_timestamp = in.readLong();
		this.timestamp = in.readLong();
		this.is_deleted = in.readByte() != 0;
	}

	public static final Creator<GlobalBotDataModel> CREATOR = new Creator<GlobalBotDataModel>() {
		@Override
		public GlobalBotDataModel createFromParcel(Parcel source) {
			return new GlobalBotDataModel(source);
		}

		@Override
		public GlobalBotDataModel[] newArray(int size) {
			return new GlobalBotDataModel[size];
		}
	};
}
