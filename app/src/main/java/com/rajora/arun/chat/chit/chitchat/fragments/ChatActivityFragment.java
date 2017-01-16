package com.rajora.arun.chat.chit.chitchat.fragments;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder;
import com.google.gson.JsonObject;
import com.rajora.arun.chat.chit.chitchat.AssistantBot.setAlarmIntentDetector;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.R.array;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.R.layout;
import com.rajora.arun.chat.chit.chitchat.R.menu;
import com.rajora.arun.chat.chit.chitchat.R.string;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_chats;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChat;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.services.SendMessageService;
import com.rajora.arun.chat.chit.chitchat.services.SetReadMessageIntentService;
import com.rajora.arun.chat.chit.chitchat.services.UpdateBotDetailsIntentService;
import com.rajora.arun.chat.chit.chitchat.utils.IntentUtils;
import com.rajora.arun.chat.chit.chitchat.utils.MessageUtils;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.ACTION_PICK;


public class ChatActivityFragment extends Fragment implements LoaderCallbacks<Cursor>{


	final static int REQUEST_CAPTURE_IMAGE=1;
	final static int REQUEST_PICK_IMAGE=2;
	final static int REQUEST_CAPTURE_VIDEO=3;
	final static int REQUEST_PICK_VIDEO=4;
	final static int REQUEST_PICK_FILE=5;
	final static int REQUEST_PICK_CONTACT=6;
	final static int REQUEST_PICK_LOCATION=7;

	private boolean mAssistantBotLastCanceled;
	private boolean mAssistantBotVisibility;
	private String ph_no;
	private ContactItemDataModel contactData;
	private FirebaseBotsDataModel mBotData;
	private int mRecyclerViewPosition;
	private int mLast_item;
	private LinearLayoutManager mLayoutManager;
	private boolean is_first_load;
	private static final int CURSOR_LOADER_ID=200;

	private adapter_chats mAdapter;
	private RecyclerView mRecyclerView;
	private CardView mAssistantBotContainer;

	private OnLayoutChangeListener mLayoutChangeListener=new OnLayoutChangeListener() {
		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if(bottom<oldBottom){
				if(mAdapter.getItemCount()>0)
					mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount()-1);
			}
		}
	};

	private AdapterDataObserver mObserver= new AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			if(is_first_load && mRecyclerViewPosition>=0){
				if(mAdapter.getItemCount()>0)
					mRecyclerView.scrollToPosition(mRecyclerViewPosition);
				is_first_load=false;
			}
			else if(is_first_load){
				if(mAdapter.getItemCount()>0)
					mRecyclerView.scrollToPosition(mLayoutManager.getItemCount()-1);
			}
			else if(!is_first_load &&
					mLayoutManager.findLastCompletelyVisibleItemPosition()==mLast_item){
				if(mAdapter.getItemCount()>0)
					mRecyclerView.scrollToPosition(mLayoutManager.getItemCount()-1);
			}
			mLast_item=mLayoutManager.findLastCompletelyVisibleItemPosition();
		}
	};

	public ChatActivityFragment() {
	}

	@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	    mLast_item=-1;
	    is_first_load=true;
        Bundle extras = getActivity().getIntent().getExtras();
	    mRecyclerViewPosition=-1;
		if(savedInstanceState!=null){
			if(savedInstanceState.containsKey("bot_data_model")){
				mBotData=savedInstanceState.getParcelable("bot_data_model");
			}
			contactData=savedInstanceState.getParcelable("contact_data");
			mRecyclerViewPosition=savedInstanceState.getInt("recycler_view_position");
			mAssistantBotLastCanceled=savedInstanceState.getBoolean("assistantBotLastCancelled");
			mAssistantBotVisibility=savedInstanceState.getBoolean("assistantBotVisibility");
		}
	    else if(extras!=null){
			if ("bot_data_model".equals(extras.getString("type"))) {
				mBotData = extras.getParcelable("data");
				contactData=new ContactItemDataModel(mBotData.getGid(),true);
			}
			else {
				contactData = extras.getParcelable("data");
			}
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    View view=inflater.inflate(layout.fragment_chat, container, false);
	    ph_no=getContext().getSharedPreferences("user-details",MODE_PRIVATE).getString("phone","");
	    mAssistantBotContainer=(CardView) view.findViewById(id.assistant_bot_container);
	    mRecyclerView = (RecyclerView) view.findViewById(id.chat_list_recycler_view);
	    mLayoutManager=new LinearLayoutManager(getActivity());
	    mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new adapter_chats(getContext(),ph_no,null, ContractChat._ID);
        mRecyclerView.setAdapter(mAdapter);
		mAssistantBotContainer.setVisibility(mAssistantBotVisibility?View.GONE:View.VISIBLE);
	    mAssistantBotContainer.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    mAssistantBotLastCanceled=true;
			    mAssistantBotVisibility=false;
			    Intent alarmIntent=new Intent(AlarmClock.ACTION_SET_ALARM).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    if (alarmIntent.resolveActivity(getContext().getPackageManager()) != null) {
				    startActivity(alarmIntent);
			    }
			    else{
				    Toast.makeText(getContext(),"No Alarm app found!",Toast.LENGTH_SHORT).show();
			    }
			    mAssistantBotContainer.setVisibility(View.GONE);
		    }
	    });
	    view.findViewById(id.assistant_bot_cancel).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    mAssistantBotVisibility= false;
			    mAssistantBotLastCanceled=true;
			    mAssistantBotContainer.setVisibility(View.GONE);
		    }
	    });

	    final EditText sendMessageEditText= (EditText) view.findViewById(id.message);
        if(savedInstanceState!=null && savedInstanceState.containsKey("send-message-text")){
            sendMessageEditText.setText(savedInstanceState.getString("send-message-text",""));
        }
        view.findViewById(id.send_message_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sendMessageEditText.getText()!=null &&
		                !sendMessageEditText.getText().toString().isEmpty()){
	                if(mBotData!=null){
		                UpdateBotDetailsIntentService.startBotDetailsUpdate(getContext(),mBotData);
		                mBotData=null;
	                }
                    SendMessageService.startSendTextMessage(getContext(),ph_no,
		                    contactData.getContact_id(),
                            sendMessageEditText.getText().toString(),
                            "text", contactData.is_bot(),utils.getCurrentTimestamp());
                }
                sendMessageEditText.setText("");
            }
        });


	    getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);

	    return view;
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_upload,menu);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if(requestCode==500 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
			IntentUtils.fireLocationPickerIntent(getContext(),getActivity(),REQUEST_PICK_LOCATION);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case id.action_upload_image:IntentUtils.showImageSelectionPopup(getContext(),REQUEST_CAPTURE_IMAGE,REQUEST_PICK_VIDEO);
				break;
			case id.action_upload_video:IntentUtils.showVideoSelectionPopup(getContext(),REQUEST_CAPTURE_VIDEO,REQUEST_PICK_VIDEO);
				break;
			case id.action_upload_file:IntentUtils.fireFilePickerIntent(getContext(),REQUEST_PICK_FILE);
				break;
			case id.action_Update_location:
				if (ContextCompat.checkSelfPermission(getActivity(), permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION},500);
				}
				else{
					IntentUtils.fireLocationPickerIntent(getContext(),getActivity(),REQUEST_PICK_LOCATION);
				}
				break;
			case id.action_upload_contact:IntentUtils.fireContactPickerIntent(getContext(),REQUEST_PICK_CONTACT);
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri=null;
		String type=null;
		if(resultCode== Activity.RESULT_OK){

			switch (requestCode){
				case REQUEST_CAPTURE_IMAGE:
				case REQUEST_PICK_IMAGE:MessageUtils.sendFileDetails(getContext(),ph_no,contactData,data.getData(),"image");
					break;
				case REQUEST_CAPTURE_VIDEO:
				case REQUEST_PICK_VIDEO:MessageUtils.sendFileDetails(getContext(),ph_no,contactData,data.getData(),"video");
					break;
				case REQUEST_PICK_FILE:MessageUtils.sendFileDetails(getContext(),ph_no,contactData,data.getData(),"file");
					break;
				case REQUEST_PICK_CONTACT:MessageUtils.sendContactDetails(getContext(),ph_no,contactData,data.getData());
					break;
				case REQUEST_PICK_LOCATION:MessageUtils.sendLocationDetails(getContext(),ph_no,contactData,PlacePicker.getPlace(getContext(),data));
					break;
			}
		}
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        if(mBotData!=null){
		    outState.putParcelable("bot_data_model",mBotData);
	    }
		outState.putBoolean("assistantBotLastCancelled",mAssistantBotLastCanceled);
		outState.putBoolean("assistantBotVisibility",mAssistantBotVisibility);
		outState.putParcelable("contact_data",contactData);
		outState.putInt("recycler_view_position", mLayoutManager.findFirstVisibleItemPosition());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.registerAdapterDataObserver(mObserver);
		mRecyclerView.addOnLayoutChangeListener(mLayoutChangeListener);
		SetReadMessageIntentService.setMessageRead(getContext(),
				contactData.getContact_id(),contactData.is_bot());
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.unregisterAdapterDataObserver(mObserver);
		mRecyclerView.removeOnLayoutChangeListener(mLayoutChangeListener);
	}

	@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), ChatContentProvider.CHAT_URI,
                new String[]{ContractChat._ID,
		                ContractChat.COLUMN_CONTACT_ID,
		                ContractChat.COLUMN_IS_BOT,
		                ContractChat.COLUMN_MESSAGE,
		                ContractChat.COLUMN_MESSAGE_DIRECTION,
		                ContractChat.COLUMN_MESSAGE_TYPE,
		                ContractChat.COLUMN_MESSAGE_STATUS,
		                ContractChat.COLUMN_TIMESTAMP,
                        ContractChat.COLUMN_UPLOAD_STATUS,
                        ContractChat.COLUMN_EXTRA_URI},
		        ContractChat.COLUMN_CONTACT_ID+" = ? AND "+ContractChat.COLUMN_IS_BOT+" = ? " ,
		        new String[]{contactData.getContact_id(),contactData.is_bot()?"1":"0"}
                ,ContractChat.COLUMN_TIMESTAMP+" ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
	    if(mBotData!=null && data!=null && data.getCount()>0){
		    mBotData=null;
	    }
	    if(data!=null &&  data.getCount()>0 && data.moveToLast()){
			String msg=data.getString(data.getColumnIndex(ContractChat.COLUMN_MESSAGE));
		    String msgType=data.getString(data.getColumnIndex(ContractChat.COLUMN_MESSAGE_TYPE));
		    if(!mAssistantBotLastCanceled && setAlarmIntentDetector.shouldShowAlarmIntent(msg,msgType)){
			    mAssistantBotContainer.setVisibility(View.VISIBLE);
			    mAssistantBotLastCanceled=false;
			    mAssistantBotVisibility=true;
		    }
	    }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
