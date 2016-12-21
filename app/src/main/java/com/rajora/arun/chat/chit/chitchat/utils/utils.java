package com.rajora.arun.chat.chit.chitchat.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by arc on 20/10/16.
 */

public class utils {
    public static long getCurrentTimestamp(){
        return System.currentTimeMillis();
    }
    public static String getTimeFromTimestamp(String sTimestamp,boolean newLine){
        long timestamp=Long.parseLong(sTimestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a"+(newLine?"\n":"")+" d-MMM");
        return format.format(c.getTime());
    }
}
