package com.rajora.arun.chat.chit.chitchatdevelopers.recyclerview_adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.R.id;
import com.rajora.arun.chat.chit.chitchatdevelopers.R.layout;
import com.rajora.arun.chat.chit.chitchatdevelopers.dataModel.LocalBotDataModel;
import com.rajora.arun.chat.chit.chitchatdevelopers.recyclerview_adapters.adapter_bot_item.VH;
import com.rajora.arun.chat.chit.chitchatdevelopers.utils.ImageUtils;


public class adapter_bot_item extends CursorRecyclerViewAdapter<VH>{

    private onItemClickListener mItemClickListener;
	private Context mContext;
    public adapter_bot_item(Context context,onItemClickListener listener,Cursor cursor,String idColumn)
    {
        super(cursor,idColumn);
	    mContext=context;
        mItemClickListener=listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(layout.view_bot_item,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, Cursor cursor) {
		LocalBotDataModel item=new LocalBotDataModel(cursor);
	    holder.mName.setText(item.name);
	    holder.mAbout.setText(item.desc);
	    if(item.image_url!=null){
		    Glide.with(mContext)
				    .load(Uri.parse(item.image_url))
				    .centerCrop()
				    .diskCacheStrategy(DiskCacheStrategy.RESULT)
				    .into(holder.mImage);
	    }
	    else{
		    ImageUtils.loadBitmapFromFirebase(mContext,"/botItem/"+item.Gid+"/botpic.png",R.drawable.empty_profile_pic,holder.mImage);
	    }
        bind(holder,item);
    }

	public void bind(final VH holder,final LocalBotDataModel item) {
		holder.itemView.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						mItemClickListener.onItemClick(holder.getAdapterPosition(),item);
					}
				}
		);
		holder.mEditImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mItemClickListener.onEditClick(holder.getAdapterPosition(),item);
			}
		});
		holder.mDeleteImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mItemClickListener.onDeleteClick(holder.getAdapterPosition(),item);
			}
		});
	}

    public static class VH extends ViewHolder{
        public CardView mImageContainerCardView;
        public ImageView mImage;
        public TextView mName;
        public TextView mAbout;
        public  ImageView mEditImageView;
        public ImageView mDeleteImageView;

        public VH(View itemView) {
            super(itemView);
            mImageContainerCardView = (CardView) itemView.findViewById(id.bot_item_image_container);
            mImage = (ImageView) itemView.findViewById(id.bot_item_image);
            mName = (TextView) itemView.findViewById(id.bot_item_name);
            mAbout = (TextView) itemView.findViewById(id.bot_item_about);
            mEditImageView= (ImageView) itemView.findViewById(id.bot_item_edit);
	        mDeleteImageView= (ImageView) itemView.findViewById(id.bot_item_delete);

        }
    }

    public interface onItemClickListener{
        void onItemClick(int position, LocalBotDataModel item);
        void onDeleteClick(int position, LocalBotDataModel item);
	    void onEditClick(int position, LocalBotDataModel item);

    }
}

