package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.AdapterChatItem.VH;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatListDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;
import com.rajora.arun.chat.chit.chitchat.utils.Utils;


public class AdapterChatItem extends CursorRecyclerViewAdapter<VH> {

	public onItemClickListener mItemClickListener;
	public Context mContext;
	public FirebaseStorage firebaseStorage;

	public AdapterChatItem(Context context, FirebaseStorage fs, onItemClickListener listener, Cursor cursor, String idColumn) {
		super(cursor, idColumn);
		mContext = context;
		firebaseStorage = fs;
		mItemClickListener = listener;
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_item, parent, false);
		return new VH(v);
	}


	@Override
	public void onBindViewHolder(VH holder, Cursor cursor) {
		ChatListDataModel item = new ChatListDataModel(cursor);

		ImageUtils.loadImageIntoView(mContext, item, holder.mImage);
		if (item.unread_count == 0) {
			holder.mUnreadCount.setVisibility(View.GONE);
		} else {
			holder.mUnreadCount.setText(String.valueOf(item.unread_count));
			holder.mUnreadCount.setVisibility(View.VISIBLE);
			holder.mUnreadCount.setContentDescription(String.format(mContext.getString(R.string.unread_message_cd_res), item.unread_count));
		}
		holder.mName.setText(item.name == null || item.name.isEmpty() ? item.contact_id : item.name);
		holder.mTime.setText(Utils.getTimeFromTimestamp(item.last_message_time, true));
		holder.itemView.setContentDescription(String.format(mContext.getString(R.string.contact_res_str), item.name == null || item.name.isEmpty() ? item.contact_id : item.name));
		holder.mImage.setContentDescription(String.format(mContext.getString(R.string.ppic_cd_res), item.name == null || item.name.isEmpty() ? item.contact_id : item.name));
		holder.mTime.setContentDescription(String.format(mContext.getString(R.string.last_msg_at_cd_str), Utils.getTimeFromTimestamp(item.last_message_time, true)));
		if (item.last_message_type != null) {
			if (item.last_message_type.equals("text")) {
				holder.mLastMessage.setText(item.last_message);
				holder.mLastMessage.setContentDescription(String.format(mContext.getString(R.string.last_msg_is_cd_str), item.last_message));
			} else {
				holder.mLastMessage.setText(String.format("[%s]", item.last_message_type));
				holder.mLastMessage.setContentDescription(String.format(mContext.getString(R.string.msg_type_cd_str), item.last_message_type));
			}
		}
		holder.mName.setContentDescription(String.format(mContext.getString(R.string.contact_cd_res), item.name == null || item.name.isEmpty() ? item.contact_id : item.name));
		bind(holder, mItemClickListener, item, holder.mImageContainerCardView);
	}

	public void bind(final VH holder, final onItemClickListener mItemClickListener, final ChatListDataModel item, final CardView img) {
		holder.itemView.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mItemClickListener.onItemClick(holder.getAdapterPosition(), item, img);
					}
				}
		);
		holder.itemView.findViewById(id.chat_item_image_container).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mItemClickListener.onImageClick(holder.getAdapterPosition(), item, img);
			}
		});
	}

	public interface onItemClickListener {
		void onItemClick(int position, ChatListDataModel item, CardView img);

		void onImageClick(int position, ChatListDataModel item, CardView img);
	}

	public static class VH extends ViewHolder {

		ImageView mImage;
		TextView mName;
		TextView mLastMessage;
		TextView mTime;
		TextView mUnreadCount;
		CardView mImageContainerCardView;

		public VH(View itemView) {
			super(itemView);
			mImageContainerCardView = (CardView) itemView.findViewById(id.chat_item_image_container);
			mImage = (ImageView) itemView.findViewById(id.chat_item_image);
			mName = (TextView) itemView.findViewById(id.chat_item_name);
			mLastMessage = (TextView) itemView.findViewById(id.chat_item_last_message);
			mTime = (TextView) itemView.findViewById(id.chat_item_time);
			mUnreadCount = (TextView) itemView.findViewById(id.chat_unread_count);
		}
	}
}
