package com.rajora.arun.chat.chit.chitchat.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

public class FirebaseBotsDataModel implements Parcelable {
	
	private String Gid;
	private String desc;
	private String dev_name;
	private String dev_no;
	private long image_last_update_timestamp;
	private String image_url;
	private String name;
	private long timestamp;
	private boolean is_deleted;

	public FirebaseBotsDataModel() {
	}

	public FirebaseBotsDataModel(String gid, String desc, String dev_name, String dev_no, long image_last_update_timestamp, String image_url, String name, long timestamp, boolean is_deleted) {
		Gid = gid;
		this.desc = desc;
		this.dev_name = dev_name;
		this.dev_no = dev_no;
		this.image_last_update_timestamp = image_last_update_timestamp;
		this.image_url = image_url;
		this.name = name;
		this.timestamp = timestamp;
		this.is_deleted = is_deleted;
	}

	public String getGid() {
		return Gid;
	}

	public void setGid(String gid) {
		Gid = gid;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDev_name() {
		return dev_name;
	}

	public void setDev_name(String dev_name) {
		this.dev_name = dev_name;
	}

	public String getDev_no() {
		return dev_no;
	}

	public void setDev_no(String dev_no) {
		this.dev_no = dev_no;
	}

	public long getImage_last_update_timestamp() {
		return image_last_update_timestamp;
	}

	public void setImage_last_update_timestamp(long image_last_update_timestamp) {
		this.image_last_update_timestamp = image_last_update_timestamp;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean is_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.Gid);
		dest.writeString(this.desc);
		dest.writeString(this.dev_name);
		dest.writeString(this.dev_no);
		dest.writeLong(this.image_last_update_timestamp);
		dest.writeString(this.image_url);
		dest.writeString(this.name);
		dest.writeLong(this.timestamp);
		dest.writeByte(this.is_deleted ? (byte) 1 : (byte) 0);
	}

	protected FirebaseBotsDataModel(Parcel in) {
		this.Gid = in.readString();
		this.desc = in.readString();
		this.dev_name = in.readString();
		this.dev_no = in.readString();
		this.image_last_update_timestamp = in.readLong();
		this.image_url = in.readString();
		this.name = in.readString();
		this.timestamp = in.readLong();
		this.is_deleted = in.readByte() != 0;
	}

	public static final Parcelable.Creator<FirebaseBotsDataModel> CREATOR = new Parcelable.Creator<FirebaseBotsDataModel>() {
		@Override
		public FirebaseBotsDataModel createFromParcel(Parcel source) {
			return new FirebaseBotsDataModel(source);
		}

		@Override
		public FirebaseBotsDataModel[] newArray(int size) {
			return new FirebaseBotsDataModel[size];
		}
	};
}
