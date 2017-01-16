package com.rajora.arun.chat.chit.chitchat.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arc on 24/12/16.
 */

public class ContactItemDataModel implements Parcelable {
    private String contact_id;
    private boolean is_bot;

    public ContactItemDataModel() {
    }

    public ContactItemDataModel(String contact_id, boolean is_bot) {
        this.contact_id = contact_id;
        this.is_bot = is_bot;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public boolean is_bot() {
        return is_bot;
    }

    public void setIs_bot(boolean is_bot) {
        this.is_bot = is_bot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contact_id);
        dest.writeByte(this.is_bot ? (byte) 1 : (byte) 0);
    }

    protected ContactItemDataModel(Parcel in) {
        this.contact_id = in.readString();
        this.is_bot = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ContactItemDataModel> CREATOR = new Parcelable.Creator<ContactItemDataModel>() {
        @Override
        public ContactItemDataModel createFromParcel(Parcel source) {
            return new ContactItemDataModel(source);
        }

        @Override
        public ContactItemDataModel[] newArray(int size) {
            return new ContactItemDataModel[size];
        }
    };
}
