package com.rajora.arun.chat.chit.chitchat.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.widget.RemoteViews;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.MainActivity;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;

public class ChatAppWidget extends AppWidgetProvider {

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
	                            int appWidgetId,int[] appwidgetIds) {
		RemoteViews remoteViews=
				new RemoteViews(context.getPackageName(),R.layout.chat_app_widget);
		remoteViews.setEmptyView(R.id.widget_linear,R.id.ListViewEmpty);

		Intent headingIntent=new Intent(context, MainActivity.class);
		PendingIntent pendingIntent=
				PendingIntent.getActivity(context,0,headingIntent,0);
		remoteViews.setOnClickPendingIntent(R.id.heading_widget,pendingIntent);

		Intent intent=new Intent(context,ChatListRemoteViewService.class);
		remoteViews.setRemoteAdapter(R.id.widget_linear,intent);

		Intent intentTemplate=new Intent(context, ChatActivity.class);
		pendingIntent= TaskStackBuilder.create(context).
				addNextIntentWithParentStack(intentTemplate).
				getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setPendingIntentTemplate(R.id.widget_linear,pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context,appWidgetManager,appWidgetId,appWidgetIds);
		}
	}

	@Override
	public void onEnabled(Context context) {
	}

	@Override
	public void onDisabled(Context context) {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if(ChatContentProvider.BROADCAST_STRING.equals(intent.getAction())){
			int[] ids=AppWidgetManager.getInstance(context)
					.getAppWidgetIds(new ComponentName(context.getPackageName(),
							ChatAppWidget.class.getName()));
			AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids,R.id.widget_linear);
		}
	}
}

