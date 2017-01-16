package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Text;
import com.google.gson.JsonObject;
import com.rajora.arun.chat.chit.chitchat.BuildConfig;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.R.layout;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_chats.VH;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class adapter_chats extends CursorRecyclerViewAdapter<VH>{

    public String mSender_No;
	private Context mContext;

    public adapter_chats(Context context,String sender_no,Cursor cursor, String idColumn) {
        super(cursor, idColumn);
        mSender_No=sender_no;
	    mContext=context;
    }

    @Override
    public void onBindViewHolder(VH holderItem, Cursor cursor) {
        ChatItemDataModel item=new ChatItemDataModel(cursor);

	    switch (item.message_type){
		    case "text":handleText((VH_TEXT) holderItem,item);
			    break;
		    case "image":handleImage((VH_IMAGE) holderItem,item);
			    break;
		    case "video":handleVideo((VH_VIDEO) holderItem,item);
			    break;
		    case "file":handleFile((VH_FILE) holderItem,item);
			    break;
		    case "contact":handleContact((VH_CONTACT) holderItem,item);
			    break;
		    case "location":handleLocation((VH_LOCATION) holderItem,item);
			    break;
	    }
    }

	private void handleText(VH_TEXT holder,ChatItemDataModel item){
		holder.itemView.setContentDescription("message "+
				item.message+ " . Time "+ utils.getTimeFromTimestamp(item.timestamp,false));
		holder.mMessage.setText(item.message);
		holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp,false));
	}

	private void handleImage(VH_IMAGE holder,ChatItemDataModel item){

	}

	private void handleVideo(VH_VIDEO holder,ChatItemDataModel item){

	}

	private void handleFile(VH_FILE holder,ChatItemDataModel item){

	}

	private void handleLocation(VH_LOCATION holder,ChatItemDataModel item){
		try {
			JSONObject jsonObject=new JSONObject(item.message);
			String name=jsonObject.getString("name");
			final double lat=jsonObject.getDouble("latitude");
			final double lon=jsonObject.getDouble("longitude");
			holder.mLocationName.setText(name==null?"":name);
			holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp,false));
			Glide.with(mContext).load("https://maps.googleapis.com/maps/api/staticmap?center="+String.valueOf(lat)+","+String.valueOf(lon)+"&zoom=17&size=600x600&maptype=roadmap" +
					"&markers=color:blue%7Clabel:P%7C"+String.valueOf(lat)+","+String.valueOf(lon)+
					"&key="+BuildConfig.MAPS_API_SECRET).centerCrop().into(holder.mLocationImage);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US,"geo:<%.8f>,<%.8f>?q=<%.8f>,<%.8f>", lat, lon,lat,lon)));
					if (intent.resolveActivity(mContext.getPackageManager()) != null) {
						mContext.startActivity(Intent.createChooser(intent, "Select an application"));;
					}
					else{
						Toast.makeText(mContext,"No map app found!",Toast.LENGTH_SHORT).show();
					}

				}
			});
		} catch (JSONException e) {
			Log.d("findme", "handleLocation: parsing error");
		}
		holder.itemView.setContentDescription("location");
	}

	private void handleContact(VH_CONTACT holder,ChatItemDataModel item){
		try {
			JSONObject jsonObject=new JSONObject(item.message);
			final String name=jsonObject.getString("name");
			final String number=jsonObject.getString("number");
			holder.mName.setText(name==null?"":name);
			holder.mNumber.setText(number==null?"":number);
			holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp,false));
			holder.mAddToContactButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Intent.ACTION_INSERT);
					intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
					intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);

					if (intent.resolveActivity(mContext.getPackageManager()) != null) {
						mContext.startActivity(intent);
					}
					else{
						Toast.makeText(mContext,"No contact app found!",Toast.LENGTH_SHORT).show();
					}

				}
			});
		} catch (JSONException e) {
			Log.d("findme", "handleLocation: parsing error");
		}
		holder.itemView.setContentDescription("contact");
	}



    @Override
    public int getItemViewType(int position) {
        if(mCursor!=null && mCursor.moveToPosition(position)){
	        int type=0;
	        switch (mCursor.getString(mCursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_TYPE))){
		        case "text":type=1;
			        break;
		        case "image":type=3;
			        break;
		        case "video":type=5;
			        break;
		        case "file":type=7;
			        break;
		        case "contact":type=9;
			        break;
		        case "location":type=11;
			        break;
	        }
	        return type+(mCursor.getString(mCursor.getColumnIndex(ContractChat.COLUMN_MESSAGE_DIRECTION)).equals("sent")?0:1);
        }
        return 0;
    }



    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
	    switch (viewType){
		    case 1:return  new VH_TEXT(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_send_text,parent,false));
		    case 2:return  new VH_TEXT(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_receive_text,parent,false));
		    case 3:return  new VH_IMAGE(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_send_image,parent,false));
		    case 4:return  new VH_IMAGE(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_receive_image,parent,false));
		    case 5:return  new VH_VIDEO(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_send_video,parent,false));
		    case 6:return  new VH_VIDEO(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_receive_video,parent,false));
		    case 7:return  new VH_FILE(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_send_file,parent,false));
		    case 8:return  new VH_FILE(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_receive_file,parent,false));
		    case 9:return  new VH_CONTACT(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_send_contact,parent,false));
		    case 10:return  new VH_CONTACT(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_receive_contact,parent,false));
		    case 11:return  new VH_LOCATION(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_send_location,parent,false));
		    case 12:return  new VH_LOCATION(LayoutInflater.from(parent.getContext()).inflate(layout.view_chat_receive_location,parent,false));
		    default:return null;
	    }
    }

    public static class VH extends ViewHolder{

        public VH(View itemView) {
            super(itemView);
        }
    }
	public static class VH_TEXT extends VH{

		TextView mMessage;
		TextView mTime;

		public VH_TEXT(View itemView) {
			super(itemView);
			mMessage = (TextView) itemView.findViewById(id.text_message);
			mTime = (TextView) itemView.findViewById(id.text_time);

		}
	}
	public static class VH_CONTACT extends VH{

		TextView mName;
		TextView mNumber;
		TextView mTime;
		Button mAddToContactButton;

		public VH_CONTACT(View itemView) {
			super(itemView);
			mName = (TextView) itemView.findViewById(id.contact_name);
			mNumber = (TextView) itemView.findViewById(id.contact_number);
			mTime = (TextView) itemView.findViewById(id.contact_time);
			mAddToContactButton = (Button) itemView.findViewById(id.add_to_contacts);
		}
	}
	public static class VH_IMAGE extends VH{

		TextView mMessage;
		TextView mTime;

		public VH_IMAGE(View itemView) {
			super(itemView);
			mMessage = (TextView) itemView.findViewById(id.text_message);
			mTime = (TextView) itemView.findViewById(id.text_time);

		}
	}
	public static class VH_VIDEO extends VH{

		TextView mMessage;
		TextView mTime;

		public VH_VIDEO(View itemView) {
			super(itemView);
			mMessage = (TextView) itemView.findViewById(id.text_message);
			mTime = (TextView) itemView.findViewById(id.text_time);

		}
	}
	public static class VH_LOCATION extends VH{

		TextView mLocationName;
		TextView mTime;
		ImageView mLocationImage;

		public VH_LOCATION(View itemView) {
			super(itemView);
			mLocationName = (TextView) itemView.findViewById(id.location_name);
			mTime = (TextView) itemView.findViewById(id.location_time);
			mLocationImage=(ImageView) itemView.findViewById(id.location_background_image);

		}
	}
	public static class VH_FILE extends VH{

		TextView mMessage;
		TextView mTime;

		public VH_FILE(View itemView) {
			super(itemView);
			mMessage = (TextView) itemView.findViewById(id.text_message);
			mTime = (TextView) itemView.findViewById(id.text_time);

		}
	}
}
