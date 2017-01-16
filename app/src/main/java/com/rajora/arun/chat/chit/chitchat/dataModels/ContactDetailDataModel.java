package com.rajora.arun.chat.chit.chitchat.dataModels;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;

/**
 * Created by arc on 4/1/17.
 */

public class ContactDetailDataModel implements Parcelable {

	public long id;
	public String contact_id;
	public boolean is_bot;
	public String name;
	public String about;
	public boolean is_user;
	public String dev_name;
	public String pic_url;
	public String pic_uri;

	public ContactDetailDataModel() {
	}

	public ContactDetailDataModel(long id, String contact_id, boolean is_bot, String name, String about, boolean is_user, String dev_name, String pic_url, String pic_uri) {
		this.id = id;
		this.contact_id = contact_id;
		this.is_bot = is_bot;
		this.name = name;
		this.about = about;
		this.is_user = is_user;
		this.dev_name = dev_name;
		this.pic_url = pic_url;
		this.pic_uri = pic_uri;
	}

	public ContactDetailDataModel(Cursor cursor){
		if(cursor!=null){
			if(cursor.getColumnIndex(ContractContacts._ID)>=0){
				id=cursor.getLong(cursor.getColumnIndex(ContractContacts._ID));
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_CONTACT_ID)>=0){
				contact_id=cursor.getString(cursor.getColumnIndex(ContractContacts.COLUMN_CONTACT_ID));
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_IS_BOT)>=0){
				is_bot=cursor.getInt(cursor.getColumnIndex(ContractContacts.COLUMN_IS_BOT))!=0;
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_NAME)>=0){
				name=cursor.getString(cursor.getColumnIndex(ContractContacts.COLUMN_NAME));
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_ABOUT)>=0){
				about=cursor.getString(cursor.getColumnIndex(ContractContacts.COLUMN_ABOUT));
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_IS_USER)>=0){
				is_user=cursor.getInt(cursor.getColumnIndex(ContractContacts.COLUMN_IS_USER))!=0;
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_DEV_NAME)>=0){
				dev_name=cursor.getString(cursor.getColumnIndex(ContractContacts.COLUMN_DEV_NAME));
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_PIC_URL)>=0){
				pic_url=cursor.getString(cursor.getColumnIndex(ContractContacts.COLUMN_PIC_URL));
			}
			if(cursor.getColumnIndex(ContractContacts.COLUMN_PIC_URI)>=0){
				pic_uri=cursor.getString(cursor.getColumnIndex(ContractContacts.COLUMN_PIC_URI));
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
		dest.writeString(this.about);
		dest.writeByte(this.is_user ? (byte) 1 : (byte) 0);
		dest.writeString(this.dev_name);
		dest.writeString(this.pic_url);
		dest.writeString(this.pic_uri);
	}

	protected ContactDetailDataModel(Parcel in) {
		this.id = in.readLong();
		this.contact_id = in.readString();
		this.is_bot = in.readByte() != 0;
		this.name = in.readString();
		this.about = in.readString();
		this.is_user = in.readByte() != 0;
		this.dev_name = in.readString();
		this.pic_url = in.readString();
		this.pic_uri = in.readString();
	}

	public static final Parcelable.Creator<ContactDetailDataModel> CREATOR = new Parcelable.Creator<ContactDetailDataModel>() {
		@Override
		public ContactDetailDataModel createFromParcel(Parcel source) {
			return new ContactDetailDataModel(source);
		}

		@Override
		public ContactDetailDataModel[] newArray(int size) {
			return new ContactDetailDataModel[size];
		}
	};
}
