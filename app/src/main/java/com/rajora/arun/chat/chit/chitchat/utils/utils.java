package com.rajora.arun.chat.chit.chitchat.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;

import com.google.firebase.crash.FirebaseCrash;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class utils {

    public static boolean canCursorMoveToFirst(Cursor cursor){
        return cursor!=null &&  cursor.getCount()>0 && cursor.moveToFirst();
    }

    public static long getCurrentTimestamp(){
        return System.currentTimeMillis();
    }

    public static String getTimeFromTimestamp(String sTimestamp,boolean newLine){
	    return DateUtils.getRelativeTimeSpanString(Long.getLong(sTimestamp),getCurrentTimestamp(),
			    DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE).toString()
			    .replace(',','\n');
/*        long timestamp=Long.parseLong(sTimestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a"+(newLine?"\n":"")+" d-MMM",
		        Locale.getDefault());
        return format.format(c.getTime());*/
    }

    public static String getTimeFromTimestamp(long timestamp,boolean newLine){

        return DateUtils.getRelativeTimeSpanString(timestamp,getCurrentTimestamp(),
		        DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE).toString()
		        .replace(',','\n');
/*        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a"+(newLine?"\n":"")+" d-MMM",
		        Locale.getDefault());
        return format.format(c.getTime());*/
    }

    public static String convertPhoneNumberToE164(Context context,String ph_no){
        PhoneNumberUtil phoneNumberUtil=PhoneNumberUtil.getInstance();
        PhoneNumber pn = null;
        String temp_ph_no=null;
        try {
            pn=phoneNumberUtil.parse(ph_no,"");
            temp_ph_no = phoneNumberUtil.format(pn, PhoneNumberFormat.E164);
        } catch (Exception e) {
            try {
                pn = phoneNumberUtil.parse(ph_no,
                        ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE))
                                .getSimCountryIso().toUpperCase());
                temp_ph_no = phoneNumberUtil.format(pn, PhoneNumberFormat.E164);
            } catch (Exception ee) {
	            FirebaseCrash.log(ee.getStackTrace().toString());
            }
        }
        return temp_ph_no;
    }

    public static String getReadableFileSize(long size){
	    double nsize=0;
	    String suffix="";
		if(size<1024){
			nsize=size;
			suffix="Bytes";
		}
	    else if(size<1024*1024){
			nsize=size/1024.0;
			suffix="KB";
		}
	    else if(size<1024*1024*1024){
			nsize=size/(1024.0*1024.0);
			suffix="MB";
		}
	    else{
			nsize=size/(1024.0*1024.0*1024.0);
			suffix="GB";
		}
	    return new DecimalFormat("#.##").format(nsize)+" "+suffix;
    }
}
