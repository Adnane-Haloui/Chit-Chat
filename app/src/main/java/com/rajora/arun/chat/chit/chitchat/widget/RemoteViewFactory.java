package com.rajora.arun.chat.chit.chitchat.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChatList;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatListDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

public class RemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context mContext;
	private Cursor mCursor;

	public RemoteViewFactory(Context applicationContext) {
		mContext=applicationContext;
		mCursor=null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDataSetChanged() {
		if(mCursor!=null && !mCursor.isClosed()){
			mCursor.close();
		}
		mCursor=mContext.getContentResolver().query(
				ChatContentProvider.CHATS_LIST_URI,
				new String[]{ContractChatList._ID,
						ContractChatList.COLUMN_CONTACT_ID,
						ContractChatList.COLUMN_IS_BOT,
						ContractChatList.COLUMN_NAME,
						ContractChatList.COLUMN_LAST_MESSAGE,
						ContractChatList.COLUMN_LAST_MESSAGE_TYPE,
						ContractChatList.COLUMN_LAST_MESSAGE_TIMESTAMP,
						ContractChatList.COLUMN_PIC_URI,
						ContractChatList.COLUMN_PIC_URL},null,null,
				ContractChatList.COLUMN_LAST_MESSAGE_TIMESTAMP+" DESC ");
	}

	@Override
	public void onDestroy() {
		if(mCursor!=null && !mCursor.isClosed()){
			mCursor.close();
		}
	}

	@Override
	public int getCount() {
		return mCursor==null?0:mCursor.getCount();
	}

	@Override
	public RemoteViews getViewAt(int position) {

		RemoteViews remoteViews=new RemoteViews(mContext.getPackageName(), R.layout.widget_chat_item);
		BitmapRequestBuilder bitmapRequestBuilder=null;
		remoteViews.setImageViewResource(R.id.chat_item_image,R.drawable.empty_profile_pic);
		if(mCursor.moveToPosition(position))
		{
			ChatListDataModel item=new ChatListDataModel(mCursor);


			if (item.pic_url == null) {
				if (item.pic_uri != null) {
					bitmapRequestBuilder=Glide.with(mContext)
							.load(Uri.parse(item.pic_uri))
							.asBitmap()
							.diskCacheStrategy(DiskCacheStrategy.RESULT)
							.centerCrop()
							.placeholder(R.drawable.empty_profile_pic)
							.error(R.drawable.empty_profile_pic);
				}
				else {
					String imgSrc=item.is_bot? "/botItem/" + item.contact_id + "/botpic.png" :
							item.contact_id.substring(1) + "/profile/profilepic.webp";
					bitmapRequestBuilder=Glide.with(mContext)
							.using(new FirebaseImageLoader())
							.load(FirebaseStorage.getInstance().getReference(imgSrc))
							.asBitmap()
							.diskCacheStrategy(DiskCacheStrategy.RESULT)
							.centerCrop()
							.placeholder(R.drawable.empty_profile_pic)
							.error(R.drawable.empty_profile_pic);
				}
			}
			else {
				bitmapRequestBuilder=Glide.with(mContext)
						.load(FirebaseStorage.getInstance().getReference(item.pic_url))
						.asBitmap()
						.diskCacheStrategy(DiskCacheStrategy.RESULT)
						.centerCrop()
						.placeholder(R.drawable.empty_profile_pic)
						.error(R.drawable.empty_profile_pic);
			}

			if(bitmapRequestBuilder!=null){
				int imgSize= (int) mContext.getResources().getDimension(R.dimen.imageview_circular_small);
				FutureTarget futureTarget=bitmapRequestBuilder.into(imgSize,imgSize);
				try {
					remoteViews.setImageViewBitmap(R.id.chat_item_image, (Bitmap) futureTarget.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if(item.unread_count==0){
				remoteViews.setViewVisibility(R.id.chat_unread_count,View.GONE);
			}
			else{
				remoteViews.setTextViewText(R.id.chat_unread_count,String.valueOf(item.unread_count));
				remoteViews.setViewVisibility(R.id.chat_unread_count,View.GONE);
				remoteViews.setContentDescription(R.id.chat_unread_count, String.format("%d%s", item.unread_count, mContext.getString(R.string.cc_new_messages)));
			}

			remoteViews.setTextViewText(R.id.chat_item_name, item.name==null ||
					item.name.isEmpty() ?item.contact_id:item.name);
			remoteViews.setTextViewText(R.id.chat_item_time,utils.getDateTimeFromTimestamp(item.last_message_time,true));

			remoteViews.setContentDescription(R.id.chat_item_root_layout, String.format("%s%s", mContext.getString(R.string.cc_contact_cdesc),
					item.name == null || item.name.isEmpty() ? item.contact_id : item.name));
			remoteViews.setContentDescription(R.id.chat_item_image, String.format("%s%s", mContext.getString(R.string.cc_profile_pic_cd),
					item.name == null || item.name.isEmpty() ? item.contact_id : item.name));
			remoteViews.setContentDescription(R.id.chat_item_time, String.format("%s%s", mContext.getString(R.string.cc_lmat_cd),
					utils.getDateTimeFromTimestamp(item.last_message_time, true)));
			remoteViews.setContentDescription(R.id.chat_item_name, String.format("%s%s", mContext.getString(R.string.cc_contact_cdesc),
					item.name == null || item.name.isEmpty() ? item.contact_id : item.name));

			if(item.last_message_type!=null && item.last_message_type.equals("text")){
				remoteViews.setTextViewText(R.id.chat_item_last_message,item.last_message);
				remoteViews.setContentDescription(R.id.chat_item_last_message, String.format("%s%s",
						mContext.getString(R.string.cc_last_message_is_cd), item.last_message));
			}
			else{
				remoteViews.setTextViewText(R.id.chat_item_last_message, String.format("[%s]", item.last_message_type));
				remoteViews.setContentDescription(R.id.chat_item_last_message, String.format("[%s]", item.last_message_type));
			}

			Intent intent=new Intent(mContext, ChatActivity.class);
			intent.putExtra("type","contact_data_model");
			intent.putExtra("data",item.getContactItemDataModel());

			remoteViews.setOnClickFillInIntent(R.id.chat_item_root_layout,intent);

		}
		return remoteViews;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return mCursor.getLong(mCursor.getColumnIndex(ContractChatList._ID));
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
