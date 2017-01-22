package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rajora.arun.chat.chit.chitchat.BuildConfig;
import com.rajora.arun.chat.chit.chitchat.R.drawable;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.R.layout;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_chats.VH;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatItemDataModel;
import com.rajora.arun.chat.chit.chitchat.services.FirebaseFileUploadService;
import com.rajora.arun.chat.chit.chitchat.utils.FileUtils;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;


public class adapter_chats extends CursorRecyclerViewAdapter<VH>{

    public String mSender_No;
	private Context mContext;
	private Fragment mFragment;
	public ChatItemDataModel mDownloadItem;
	public String mDownloadFileName;
	public String mDownloadLocation;

    public adapter_chats(Context context,Fragment fragment,String sender_no,Cursor cursor, String idColumn) {
        super(cursor, idColumn);
        mSender_No=sender_no;
	    mContext=context;
	    mFragment=fragment;
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
		holder.itemView.setContentDescription("message "+ item.message+ " . Time "+ utils.getTimeFromTimestamp(item.timestamp,false));
		holder.mMessage.setText(item.message);
		holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp,false));
	}

	private void handleImage(final VH_IMAGE holder, final ChatItemDataModel item){
		try {
			JSONObject data=new JSONObject(item.message);
			final String fileName=data.getString("name");
			final String location=data.getString("location");
			if(item.extra_uri!=null && !item.extra_uri.isEmpty()){
				Glide.with(mContext)
						.load(Uri.parse(item.extra_uri))
						.fitCenter()
						.diskCacheStrategy(DiskCacheStrategy.RESULT)
						.into(holder.image);
			}
			else{
				StorageReference mref=null;
				if(item.message_direction.equals("sent")){
					mref=FirebaseStorage.getInstance().getReference().child(item.contact_id.substring(1)+"/"+mSender_No.substring(1)+"/"+item.chat_id+fileName);
				}
				else{
					mref=FirebaseStorage.getInstance().getReference().child(mSender_No.substring(1)+"/"+item.contact_id.substring(1)+"/"+item.chat_id+fileName);
				}
				Glide.with(mContext)
						.using(new FirebaseImageLoader())
						.load(mref)
						.fitCenter()
						.diskCacheStrategy(DiskCacheStrategy.RESULT)
						.into(holder.image);

			}
			if(item.upload_status==null){
				holder.mButton.setVisibility(View.VISIBLE);
				holder.mButton.setImageResource(drawable.download_icon);
				holder.mProgress.setVisibility(View.GONE);
				holder.itemView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
							mDownloadItem=item;
							mDownloadFileName=fileName;
							mDownloadLocation=location;
							mFragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,660);
						}
						else{
							if(item.message_direction.equals("send")){
								FirebaseFileUploadService.startReceiveFileMessage(mContext,mSender_No,item.contact_id,item.chat_id,fileName,location,item.message_type,mSender_No);
							}
							else{
								FirebaseFileUploadService.startReceiveFileMessage(mContext,item.contact_id,mSender_No,item.chat_id,fileName,location,item.message_type,mSender_No);
							}
						}

					}
				});
			}
			else{
				switch (item.upload_status){
					case "preparing":
					case "uploading":
						holder.mProgress.setVisibility(View.VISIBLE);
						holder.mButton.setVisibility(View.GONE);
						break;
					case "downloaded":
					case "uploaded":
						holder.mProgress.setVisibility(View.GONE);
						holder.mButton.setVisibility(View.GONE);
						break;
					case "downloading":
						holder.mButton.setVisibility(View.GONE);
						holder.mProgress.setVisibility(View.VISIBLE);
						break;
					case "upload_failed":
					case "download_failed":
						holder.mButton.setImageResource(drawable.ic_error_black_24dp);
						holder.mButton.setVisibility(View.VISIBLE);
						holder.mProgress.setVisibility(View.GONE);
						break;
				}
				switch (item.upload_status){
					case "preparing":
					case "uploading":
					case "downloaded":
					case "uploaded":
					case "upload_failed":
						holder.itemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.parse(item.extra_uri), "image/*");
								if (intent.resolveActivity(mContext.getPackageManager()) != null) {
									mContext.startActivity(intent);
								}
								else{
									Toast.makeText(mContext,"No app found to open this image !",Toast.LENGTH_SHORT).show();
								}
							}
						});
						break;
					case "downloading":
						holder.itemView.setOnClickListener(null);
						break;
					case "download_failed":
						holder.itemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Snackbar.make(holder.itemView,"Failed to download !",Snackbar.LENGTH_SHORT).show();
							}
						});
						break;
				}
			}
			holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp,false));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handleVideo(final VH_VIDEO holder,final ChatItemDataModel item){
		try {
			JSONObject data=new JSONObject(item.message);
			final String fileName=data.getString("name");
			long filesize=data.getLong("size");
			final String location=data.getString("location");
			holder.mVideoName.setText(fileName==null?"":fileName);
			holder.mVideoSize.setText(utils.getReadableFileSize(filesize));
			final String thumbnailUriPath=FileUtils.getPath(mContext,Uri.parse(item.extra_uri));
			if(item.extra_uri!=null && !item.extra_uri.isEmpty()){
				if(thumbnailUriPath!=null){
					Glide.with(mContext)
							.load(Uri.fromFile(new File(thumbnailUriPath)))
							.fitCenter()
							.diskCacheStrategy(DiskCacheStrategy.RESULT)
							.into(holder.mThumbnail);
				}

			}
			holder.mVideoSize.setVisibility(View.VISIBLE);
			holder.mVideoName.setVisibility(View.VISIBLE);
			if(item.upload_status==null){
				holder.mButton.setVisibility(View.VISIBLE);
				holder.mButton.setImageResource(drawable.download_icon);
				holder.mProgress.setVisibility(View.GONE);
				holder.itemView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
							mDownloadItem=item;
							mDownloadFileName=fileName;
							mDownloadLocation=location;
							mFragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,660);
						}
						else{
							if(item.message_direction.equals("send")){
								FirebaseFileUploadService.startReceiveFileMessage(mContext,mSender_No,item.contact_id,item.chat_id,fileName,location,item.message_type,mSender_No);
							}
							else{
								FirebaseFileUploadService.startReceiveFileMessage(mContext,item.contact_id,mSender_No,item.chat_id,fileName,location,item.message_type,mSender_No);
							}
						}
					}
				});
			}
			else{
				switch (item.upload_status){
					case "preparing":
					case "uploading":
						holder.mProgress.setVisibility(View.VISIBLE);
						holder.mButton.setVisibility(View.GONE);
						break;
					case "downloaded":
					case "uploaded":
						holder.mVideoSize.setVisibility(View.GONE);
						holder.mVideoName.setVisibility(View.GONE);
						holder.mProgress.setVisibility(View.GONE);
						holder.mButton.setImageResource(drawable.ic_play_circle_filled_black_24dp);
						holder.mButton.setVisibility(View.VISIBLE);
						break;
					case "downloading":
						holder.mButton.setVisibility(View.GONE);
						holder.mProgress.setVisibility(View.VISIBLE);
						break;
					case "upload_failed":
					case "download_failed":
						holder.mButton.setImageResource(drawable.ic_error_black_24dp);
						holder.mButton.setVisibility(View.VISIBLE);
						holder.mProgress.setVisibility(View.GONE);
						break;
				}
				switch (item.upload_status){
					case "preparing":
					case "uploading":
					case "downloaded":
					case "uploaded":
					case "upload_failed":
						holder.itemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.fromFile(new File(thumbnailUriPath)), "video/*");
								if (intent.resolveActivity(mContext.getPackageManager()) != null) {
									mContext.startActivity(intent);
								}
								else{
									Toast.makeText(mContext,"No app found to open this video !",Toast.LENGTH_SHORT).show();
								}
							}
						});
						break;
					case "downloading":
						holder.itemView.setOnClickListener(null);
						break;
					case "download_failed":
						holder.itemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Snackbar.make(holder.itemView,"Failed to download !",Snackbar.LENGTH_SHORT).show();
							}
						});
						break;
				}
				holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp,false));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handleFile(final VH_FILE holder,final ChatItemDataModel item){
		try {
			JSONObject data=new JSONObject(item.message);
			final String fileName=data.getString("name");
			long filesize=data.getLong("size");
			final String location=data.getString("location");
			holder.mFileName.setText(fileName==null?"":fileName);
			holder.mFileSize.setText(utils.getReadableFileSize(filesize));
			if(item.upload_status==null){
				holder.mButton.setVisibility(View.VISIBLE);
				holder.mButton.setImageResource(drawable.download_icon);
				holder.mProgress.setVisibility(View.GONE);
				holder.itemView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
							mDownloadItem=item;
							mDownloadFileName=fileName;
							mDownloadLocation=location;
							mFragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,660);
						}
						else{
							if(item.message_direction.equals("send")){
								FirebaseFileUploadService.startReceiveFileMessage(mContext,mSender_No,item.contact_id,item.chat_id,fileName,location,item.message_type,mSender_No);
							}
							else{
								FirebaseFileUploadService.startReceiveFileMessage(mContext,item.contact_id,mSender_No,item.chat_id,fileName,location,item.message_type,mSender_No);
							}
						}
					}
				});
			}
			else {
				switch (item.upload_status) {
					case "preparing":
					case "uploading":
						holder.mProgress.setVisibility(View.VISIBLE);
						holder.mButton.setVisibility(View.GONE);
						break;
					case "downloaded":
					case "uploaded":
						holder.mProgress.setVisibility(View.GONE);
						holder.mButton.setImageResource(drawable.ic_open_in_new_black_24dp);
						holder.mButton.setVisibility(View.VISIBLE);
						break;
					case "downloading":
						holder.mButton.setVisibility(View.GONE);
						holder.mProgress.setVisibility(View.VISIBLE);
						break;
					case "upload_failed":
					case "download_failed":
						holder.mButton.setImageResource(drawable.ic_error_black_24dp);
						holder.mButton.setVisibility(View.VISIBLE);
						holder.mProgress.setVisibility(View.GONE);
						break;
				}
				switch (item.upload_status) {
					case "preparing":
					case "uploading":
					case "downloaded":
					case "uploaded":
					case "upload_failed":
						holder.itemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.parse(item.extra_uri),FileUtils.getMimeType(item.extra_uri));
								if (intent.resolveActivity(mContext.getPackageManager()) != null) {
									mContext.startActivity(intent);
								}
								else{
									Toast.makeText(mContext,"No app found to open this app !",Toast.LENGTH_SHORT).show();
								}
							}
						});
						break;
					case "downloading":
						holder.itemView.setOnClickListener(null);
						break;
					case "download_failed":
						holder.itemView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Snackbar.make(holder.itemView, "Failed to download !", Snackbar.LENGTH_SHORT).show();
							}
						});
						break;
				}
				holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp, false));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handleLocation(VH_LOCATION holder,ChatItemDataModel item){
		try {
			JSONObject jsonObject=new JSONObject(item.message);
			String name=jsonObject.getString("name");
			final double lat=jsonObject.getDouble("latitude");
			final double lon=jsonObject.getDouble("longitude");
			holder.mLocationName.setText(name==null?"":name);
			holder.mTime.setText(utils.getTimeFromTimestamp(item.timestamp,false));
			Glide.with(mContext).load("https://maps.googleapis.com/maps/api/staticmap?center="+ lat +","+ lon +"&zoom=17&size=600x600&maptype=roadmap" +
					"&markers=color:blue%7Clabel:P%7C"+ lat +","+ lon +
					"&key="+BuildConfig.MAPS_API_SECRET).centerCrop().into(holder.mLocationImage);
			holder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US,"geo:<%.8f>,<%.8f>?q=<%.8f>,<%.8f>", lat, lon,lat,lon)));
					if (intent.resolveActivity(mContext.getPackageManager()) != null) {
						mContext.startActivity(Intent.createChooser(intent, "Select an application"));
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
			holder.mAddToContactButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Intent.ACTION_INSERT);
					intent.setType(Contacts.CONTENT_TYPE);
					intent.putExtra(Insert.NAME, name);
					intent.putExtra(Insert.PHONE, number);

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

		ImageView image;
		ImageView mButton;
		TextView mTime;
		ProgressBar mProgress;

		public VH_IMAGE(View itemView) {
			super(itemView);
			mButton = (ImageView) itemView.findViewById(id.image_button);
			mTime = (TextView) itemView.findViewById(id.image_time);
			image= (ImageView) itemView.findViewById(id.image_image);
			mProgress= (ProgressBar) itemView.findViewById(id.image_progressbar);
		}
	}
	public static class VH_VIDEO extends VH{

		ImageView mThumbnail;
		TextView mVideoName;
		TextView mVideoSize;
		ImageView mButton;
		TextView mTime;
		ProgressBar mProgress;

		public VH_VIDEO(View itemView) {
			super(itemView);
			mVideoName = (TextView) itemView.findViewById(id.video_name);
			mTime = (TextView) itemView.findViewById(id.video_time);
			mVideoSize=(TextView) itemView.findViewById(id.video_size);
			mThumbnail=(ImageView) itemView.findViewById(id.video_thumbnail);
			mButton=(ImageView) itemView.findViewById(id.video_button);
			mProgress= (ProgressBar) itemView.findViewById(id.video_progressbar);
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

		TextView mFileName;
		TextView mFileSize;
		ImageView mButton;
		TextView mTime;
		ProgressBar mProgress;

		public VH_FILE(View itemView) {
			super(itemView);
			mFileName = (TextView) itemView.findViewById(id.file_name);
			mFileSize = (TextView) itemView.findViewById(id.file_size);
			mTime = (TextView) itemView.findViewById(id.file_time);
			mButton=(ImageView) itemView.findViewById(id.file_button);
			mProgress= (ProgressBar) itemView.findViewById(id.file_progressbar);
		}
	}
}
