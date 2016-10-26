package com.rajora.arun.chat.chit.chitchatdevelopers.dataModel;

/**
 * Created by arc on 20/10/16.
 */

public class BotDataModel {

    public final static String S_id="id";
    public final static String S_g_id="Gid";
    public final static String S_name="name";
    public final static String S_dev_no="dev_no";
    public final static String S_dev_name="dev_name";
    public final static String S_desc="desc";
    public final static String S_endpoint="endpoint";
    public final static String S_secret="secret";
    public final static String S_image_url="image_url";
    public final static String S_image_last_update_timestamp="image_last_update_timestamp";
    public final static String S_timestamp="timestamp";
    private String name;
    private String dev_no;
    private String dev_name;
    private String desc;
    private String endpoint;
    private String secret;
    private String image_url;
    private long image_last_update_timestamp;
    static long timestamp;

    public BotDataModel() {
    }

    public BotDataModel(String name,String dev_name,String dev_no,String desc,String endpoint, String secret,String image_url) {
        this.name = name;
        this.desc = desc;
        this.dev_no=dev_no;
        this.dev_name=dev_name;
        this.endpoint=endpoint;
        this.secret=secret;
        this.image_url=image_url;
    }

}
