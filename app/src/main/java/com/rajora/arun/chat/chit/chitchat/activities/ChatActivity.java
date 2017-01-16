package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_sidebar_contact_item;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactDetailDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.services.SendMessageService;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

import java.util.HashSet;
import java.util.Set;

public class ChatActivity extends AppCompatChatListenerActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, adapter_sidebar_contact_item.onItemClickListener {

	private static final int REQUEST_PLAY_SERVICES_RESOLUTION = 9000;

	private String my_ph_no;
	private FirebaseBotsDataModel botData;
	private ContactItemDataModel contactData;


	private TextView last_seen_text_view;
	private TextView chat_name_textview;
	private ImageView chat_image_imageview;
	private AlertDialog.Builder mAlertDialog;

	private DatabaseReference onlineReference;
	private DatabaseReference connectedRef;
	private HashSet<Integer> mOpenIndexSet;

	private int mConnectedDevices;
	private int mNotUserWarningProgress;

	ValueEventListener connectedEventListener=new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			if(dataSnapshot.getValue(Boolean.class)){
				last_seen_text_view.setVisibility(View.INVISIBLE);
			}
			else{
				last_seen_text_view.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onCancelled(DatabaseError databaseError) {

		}
	};

	ChildEventListener onlineEventListener=new ChildEventListener() {
		@Override
		public void onChildAdded(DataSnapshot dataSnapshot, String s) {
			mConnectedDevices++;
			if(mConnectedDevices>0){
				last_seen_text_view.setText("Online");
				last_seen_text_view.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onChildChanged(DataSnapshot dataSnapshot, String s) {
			mConnectedDevices--;
			if(mConnectedDevices<=0){
				mConnectedDevices=0;
				last_seen_text_view.setText("Offline");
				last_seen_text_view.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onChildRemoved(DataSnapshot dataSnapshot) {
		}

		@Override
		public void onChildMoved(DataSnapshot dataSnapshot, String s) {

		}

		@Override
		public void onCancelled(DatabaseError databaseError) {

		}
	};

	private RecyclerView mRecyclerView;
	private adapter_sidebar_contact_item mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private static final int CURSOR_CONTACT_LIST_LOADER_ID = 300;
	private static final int CURSOR_CONTACT_ITEM_LOADER_ID = 400;
	private boolean mIsSidebarOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		checkPlayServices();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mNotUserWarningProgress=0;
		chat_name_textview= (TextView) findViewById(R.id.chat_name);
		chat_image_imageview= (ImageView) findViewById(R.id.chat_image);
		last_seen_text_view=(TextView) findViewById(R.id.chat_last_seen);

		findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (savedInstanceState != null) {
			restoreValuesFromBundle(savedInstanceState);
			if (savedInstanceState.containsKey("is_sidepanel_open")
					&& savedInstanceState.getBoolean("is_sidepanel_open", false)) {
				((DrawerLayout) findViewById(R.id.sidebar_drawer)).openDrawer(GravityCompat.START, false);
			}
			savedInstanceState.getInt("NotUserWarningProgress");
		}
		else if (extras != null) {
			restoreValuesFromBundle(extras);
		}
		if(mOpenIndexSet==null) {mOpenIndexSet=new HashSet<Integer>();}
		findViewById(R.id.chat_image_container).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatActivity.this, ProfileDetailsActivity.class);
				if (botData != null) {
					intent.putExtra("type", "bot_data_model");
					intent.putExtra("data", botData);
				} else {
					intent.putExtra("type", "contact_data_model");
					intent.putExtra("data", contactData);
				}
				startActivity(intent);
			}
		});

		my_ph_no = getSharedPreferences("user-details", MODE_PRIVATE).getString("phone", "");

		((DrawerLayout) findViewById(R.id.sidebar_drawer)).addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				mIsSidebarOpen = true;
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				mIsSidebarOpen = false;
			}

			@Override
			public void onDrawerStateChanged(int newState) {
			}
		});

		mRecyclerView = (RecyclerView) findViewById(R.id.sidebar_recycler_view);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		getSupportLoaderManager().initLoader(CURSOR_CONTACT_LIST_LOADER_ID, null, this);
		mAdapter = new adapter_sidebar_contact_item(this, this, null, ContractContacts._ID,mOpenIndexSet);
		mRecyclerView.setAdapter(mAdapter);

	}

	private void restoreValuesFromBundle(Bundle bundle) {

		if (bundle.getString("type").equals("bot_data_model")) {
			botData = bundle.getParcelable("data");
			chat_name_textview.setText(botData.getName());
			contactData=new ContactItemDataModel(botData.getGid(),true);
			if(botData.getImage_url()!=null){
				Glide.with(this)
						.using(new FirebaseImageLoader())
						.load(FirebaseStorage.getInstance().getReference(botData.getImage_url()))
						.into(chat_image_imageview);
				chat_name_textview.setContentDescription("Chat bot - "+botData.getName());
				chat_image_imageview.setContentDescription("Profile picture of chat bot - "+botData.getName());
			}
		} else {
			contactData = bundle.getParcelable("data");
			getSupportLoaderManager().initLoader(CURSOR_CONTACT_ITEM_LOADER_ID,null,this);
		}
		if(bundle.containsKey("last-seen")){
			last_seen_text_view.setText(bundle.getString("last-seen"));
		}
		if(bundle.containsKey("openindex")){
			mOpenIndexSet= (HashSet<Integer>) bundle.getSerializable("openindex");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(!contactData.is_bot()){
			mConnectedDevices=0;
			onlineReference= FirebaseDatabase.getInstance().getReference("online/users/"+contactData.getContact_id().substring(1));
			onlineReference.orderByChild("online").equalTo(Boolean.TRUE).addChildEventListener(onlineEventListener);
			connectedRef=FirebaseDatabase.getInstance().getReference(".info/connected");
			connectedRef.addValueEventListener(connectedEventListener);
		}
	}

	@Override
	protected void onStop() {
		if(connectedRef!=null)
			connectedRef.removeEventListener(connectedEventListener);
		mConnectedDevices=0;
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("is_sidepanel_open", mIsSidebarOpen);
		if (botData != null) {
			outState.putString("type", "bot_data_model");
			outState.putParcelable("data", botData);
		} else {
			outState.putString("type", "contact_data_model");
			outState.putParcelable("data", contactData);
		}
		if(mOpenIndexSet!=null){
			outState.putSerializable("openindex",mOpenIndexSet);
		}
		outState.putString("last-seen",last_seen_text_view.getText().toString());
		outState.putInt("NotUserWarningProgress",mNotUserWarningProgress);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(this) != null)
					finish();
				else
					NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_about:
				Intent about_intent = new Intent(this, AboutActivity.class);
				startActivity(about_intent);
				return true;
			case R.id.action_Update_Profile:
				Intent update_intent = new Intent(this, ProfileEditActivity.class);
				startActivity(update_intent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chat, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == CURSOR_CONTACT_LIST_LOADER_ID) {
			return new CursorLoader(this, ChatContentProvider.CONTACT_LIST_URI,
					new String[]{
							ContractContacts._ID,
							ContractContacts.COLUMN_CONTACT_ID,
							ContractContacts.COLUMN_NAME,
							ContractContacts.COLUMN_PIC_URI,
							ContractContacts.COLUMN_PIC_URL,
							ContractContacts.COLUMN_IS_USER},
					ContractContacts.COLUMN_IS_BOT+" = ? ", new String[]{"0"},
					ContractContacts.COLUMN_IS_USER + " DESC , " +
							ContractContacts.COLUMN_NAME + " COLLATE NOCASE ");
		}
		if (id == CURSOR_CONTACT_ITEM_LOADER_ID) {
			return new CursorLoader(this, ChatContentProvider.CONTACT_LIST_URI,
					new String[]{
							ContractContacts.COLUMN_CONTACT_ID,
							ContractContacts.COLUMN_NAME,
							ContractContacts.COLUMN_PIC_URI,
							ContractContacts.COLUMN_PIC_URI,
							ContractContacts.COLUMN_IS_USER,
							ContractContacts.COLUMN_IS_BOT},
					ContractContacts.COLUMN_CONTACT_ID+" = ? AND "+ContractContacts.COLUMN_IS_BOT+" = ? ",
					new String[]{contactData.getContact_id(),contactData.is_bot()?"1":"0"},null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == CURSOR_CONTACT_LIST_LOADER_ID) {
			mAdapter.swapCursor(data);
		}
		else if(loader.getId() == CURSOR_CONTACT_ITEM_LOADER_ID && utils.canCursorMoveToFirst(data)){
			processCursor(data);
		}
	}

	private void processCursor(Cursor cursor){
		if(utils.canCursorMoveToFirst(cursor)) {
			ContactDetailDataModel item = new ContactDetailDataModel(cursor);
			if(mNotUserWarningProgress!=2 && mAlertDialog==null){
				if(item.is_bot && !item.is_user){
					showBotDeletedDialog();
				}
				else if(!item.is_bot && !item.is_user){
					showUserNotActivatedDialog();
				}
			}
			ImageUtils.loadImageIntoView(this,item,chat_image_imageview);
			if (item.name == null || item.name.isEmpty()) {
				chat_name_textview.setText(item.contact_id);
				if (item.is_bot) {
					chat_name_textview.setContentDescription("Chat bot - " + item.contact_id);
					chat_image_imageview.setContentDescription("Profile picture of chat bot - " + item.contact_id);
				} else {
					chat_name_textview.setContentDescription("Contact - " + item.contact_id);
					chat_image_imageview.setContentDescription("Profile picture of contact - " + item.contact_id);
				}
			} else {
				chat_name_textview.setText(item.name);
				if (item.is_bot) {
					chat_name_textview.setContentDescription("Chat bot - " + item.name);
					chat_image_imageview.setContentDescription("Profile picture of chat bot - " + item.name);
				} else {
					chat_name_textview.setContentDescription("Contact - " + item.name);
					chat_image_imageview.setContentDescription("Profile picture of contact - " + item.name);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == CURSOR_CONTACT_LIST_LOADER_ID) {
			mAdapter.swapCursor(null);
		}
	}


	@Override
	public void onSendClick(int position, ContactDetailDataModel item, adapter_sidebar_contact_item.VH holder) {
		String to_id = item.contact_id;
		SendMessageService.startSendTextMessage(this, "+" + my_ph_no, to_id,
				holder.mMessage.getText().toString(), "text", false, utils.getCurrentTimestamp());
		holder.mMessage.setText("");
	}

	@Override
	public void onContactClick(int position, adapter_sidebar_contact_item.VH holder, Set<Integer> openIndexes) {
		if(openIndexes.contains(position)){
			openIndexes.remove(position);
			holder.sidebarPanel.setVisibility(View.GONE);
		}
		else{
			openIndexes.add(position);
			holder.sidebarPanel.setVisibility(View.VISIBLE);
		}

	}

	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, REQUEST_PLAY_SERVICES_RESOLUTION)
						.show();
			} else {
				new AlertDialog.Builder(this).setMessage("Google Play Services Error")
						.setTitle("This device is not supported for required Goole Play Services")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								finish();
							}
						})
						.create()
						.show();
			}
			return false;
		}
		return true;
	}

	private void showBotDeletedDialog(){
		mNotUserWarningProgress=1;
		mAlertDialog=new AlertDialog.Builder(this);
		mAlertDialog.setCancelable(false);
		mAlertDialog.setTitle("Bot Service discontinued");
		mAlertDialog.setMessage("This bot has been discontinued. Any message sent to this bot will not get any response");
		mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress=2;
				dialogInterface.dismiss();
			}
		});
		mAlertDialog.setNegativeButton("GO BACK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress=2;
				dialogInterface.dismiss();
				finish();
			}
		});
		mAlertDialog.create().show();
	}
	private void showUserNotActivatedDialog(){
		mNotUserWarningProgress=1;
		mAlertDialog=new AlertDialog.Builder(this);
		mAlertDialog.setCancelable(false);
		mAlertDialog.setTitle("User not registered");
		mAlertDialog.setMessage("This user might not have registered.Any message sent will be delivered after the user registers");
		mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress=2;
				dialogInterface.dismiss();
			}
		});
		mAlertDialog.setNeutralButton("Invite", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress=2;
				dialogInterface.dismiss();
				Intent intent = new AppInviteInvitation.IntentBuilder("Chit Chat Invitation")
						.setMessage("you have been invited to Chit Chat App by "+contactData.getContact_id()+" . https://vc8hx.app.goo.gl/naxz")
						.setDeepLink(Uri.parse("https://vc8hx.app.goo.gl/naxz"))
						.build();
				startActivity(intent);
			}
		});
		mAlertDialog.setNegativeButton("GO BACK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress=2;
				dialogInterface.dismiss();
				finish();
			}
		});
		mAlertDialog.create().show();
	}
}
