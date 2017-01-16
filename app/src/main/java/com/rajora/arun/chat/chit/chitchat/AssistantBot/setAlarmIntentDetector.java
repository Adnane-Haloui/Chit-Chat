package com.rajora.arun.chat.chit.chitchat.AssistantBot;


public class setAlarmIntentDetector {
	private static final String matchList[][]={{"meet"},{"see","at"},{"remember"},{"alarm"},{"remind"},{"wait"}};
	public static boolean shouldShowAlarmIntent(String msg,String msg_type){
		if(msg==null || msg_type==null) return false;
		if(msg_type.equals("text")){
			for(String x[]:matchList){
				int gotCound=0;
				for(String term:x){
					gotCound+= msg.toLowerCase().contains(term)?1:0;
				}
				if(gotCound==x.length)
					return true;
			}
		}
		return false;
	}
}
