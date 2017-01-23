package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;

public class bot_VH extends RecyclerView.ViewHolder {

	private final CardView mImageContainerCardView;
	private final ImageView mImage;
	private final TextView mName;
	private final TextView mDeveloperName;
	private final TextView mAbout;

	public bot_VH(View itemView) {
        super(itemView);
		mImageContainerCardView = (CardView) itemView.findViewById(R.id.bot_item_image_container);
		mImage = (ImageView) itemView.findViewById(R.id.bot_item_image);
		mName = (TextView) itemView.findViewById(R.id.bot_item_name);
		mDeveloperName = (TextView) itemView.findViewById(R.id.bot_item_developer_name);
		mAbout = (TextView) itemView.findViewById(R.id.bot_item_about);
    }

	public void setValues(FirebaseBotsDataModel model, Context context){
		mName.setText(model.getName());
		mDeveloperName.setText(model.getDev_name());
		mAbout.setText(model.getDesc());
		if(model.getImage_url()!=null){
			ImageUtils.loadBitmapFromFirebase(context,model.getImage_url(), R.drawable.empty_profile_pic,mImage);
		}
		else{
			ImageUtils.loadBitmapFromFirebase(context,"/botItem/"+model.getGid()+"/botpic.png",R.drawable.empty_profile_pic,mImage);
		}
	}

	public void setContentDescription(FirebaseBotsDataModel model){
		mName.setContentDescription("Bot item "+model.getName());
		itemView.setContentDescription("Bot item "+model.getName()+" by "+
				(model.getDev_name()==null || model.getDev_name().isEmpty() ?"Unknown":model.getDev_name()));
		mImageContainerCardView.setContentDescription("Image of "+model.getName()+" bot");
		mDeveloperName.setContentDescription("Bot developed by "+model.getDev_name());
		mAbout.setContentDescription("About "+model.getName()+" is "+
				(model.getDesc()==null || model.getDesc().isEmpty() ?"Unknown":model.getDesc()));
	}

	public void setClickListeners(final FirebaseBotsDataModel model, final Context context){
		mImageContainerCardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context, ProfileDetailsActivity.class);
				intent.putExtra("type","bot_data_model");
				intent.putExtra("data",model);
				context.startActivity(intent);
			}
		});
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context, ChatActivity.class);
				intent.putExtra("type","bot_data_model");
				intent.putExtra("data",model);
				context.startActivity(intent);
			}
		});
	}

}
