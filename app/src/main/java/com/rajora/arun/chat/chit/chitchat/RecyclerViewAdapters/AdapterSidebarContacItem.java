package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.AdapterSidebarContacItem.VH;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactDetailDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;

import java.util.Set;


public class AdapterSidebarContacItem extends CursorRecyclerViewAdapter<VH> {

	public Set<Integer> mOpenIndexes;
	private onItemClickListener mItemClickListener;
	private Context mContext;

	public AdapterSidebarContacItem(Context context, onItemClickListener listener, Cursor cursor, String idColumn, Set<Integer> openIndexes) {
		super(cursor, idColumn);
		mContext = context;
		mItemClickListener = listener;
		mOpenIndexes = openIndexes;
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_sidebar_item, parent, false);
		return new VH(v);
	}

	@Override
	public void onBindViewHolder(VH holder, Cursor cursor) {
		ContactDetailDataModel item = new ContactDetailDataModel(cursor);
		holder.sidebarPanel.setVisibility(mOpenIndexes.contains(holder.getAdapterPosition()) ? View.VISIBLE : View.GONE);
		ImageUtils.loadImageIntoView(mContext, item, holder.mImage);
		holder.mName.setText(item.name == null ? "" : item.name);
		holder.mName.setContentDescription(String.format(mContext.getString(R.string.contact_cd_str_res), item.name == null ? mContext.getString(R.string.Unknown_text) : item.name));
		holder.mImageContainerCardView.setContentDescription(String.format(mContext.getString(R.string.mpic_cd_res_str), item.name == null ? mContext.getString(R.string.Unknown_text) : item.name));
		bind(holder, mItemClickListener, item);
	}

	public void bind(final VH holder, final onItemClickListener mItemClickListener, final ContactDetailDataModel item) {
		holder.mSendButton.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mItemClickListener.onSendClick(holder.getAdapterPosition(), item, holder);
					}
				}
		);
		holder.mImageContainerCardView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mItemClickListener.onContactClick(holder.getAdapterPosition(), holder, mOpenIndexes);
			}
		});
		holder.mName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mItemClickListener.onContactClick(holder.getAdapterPosition(), holder, mOpenIndexes);
			}
		});
	}

	public interface onItemClickListener {
		void onSendClick(int position, ContactDetailDataModel item, VH holder);

		void onContactClick(int position, VH holder, Set<Integer> openIndexes);
	}

	public static class VH extends ViewHolder {

		public CardView mImageContainerCardView;
		public ImageView mImage;
		public TextView mName;
		public EditText mMessage;
		public CardView mSendButton;
		public LinearLayout sidebarPanel;

		public VH(View itemView) {
			super(itemView);

			mImageContainerCardView = (CardView) itemView.findViewById(id.sidebar_image_container);
			mImage = (ImageView) itemView.findViewById(id.sidebar_image);
			mName = (TextView) itemView.findViewById(id.sidebar_name);
			mMessage = (EditText) itemView.findViewById(id.sidebar_message);
			mSendButton = (CardView) itemView.findViewById(id.sidebar_send_message_button);
			sidebarPanel = (LinearLayout) itemView.findViewById(id.sidebar_panel);
		}
	}
}
