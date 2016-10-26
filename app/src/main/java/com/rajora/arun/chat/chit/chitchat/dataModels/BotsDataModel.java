package com.rajora.arun.chat.chit.chitchat.dataModels;

/**
 * Created by arc on 25/10/16.
 */

public class BotsDataModel {
    private String Gid;
    private String desc;
    private String dev_name;
    private String dev_no;
    private String image_last_update_timestamp;
    private String image_url;
    private String name;
    private String timestamp;

    public BotsDataModel() {
    }

    public BotsDataModel(String gid, String desc, String dev_name, String dev_no, String image_last_update_timestamp, String image_url, String name, String timestamp) {
        Gid = gid;
        this.desc = desc;
        this.dev_name = dev_name;
        this.dev_no = dev_no;
        this.image_last_update_timestamp = image_last_update_timestamp;
        this.image_url = image_url;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getGid() {
        return Gid;
    }

    public String getDesc() {
        return desc;
    }

    public String getDev_name() {
        return dev_name;
    }

    public String getDev_no() {
        return dev_no;
    }

    public String getImage_last_update_timestamp() {
        return image_last_update_timestamp;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }



}
