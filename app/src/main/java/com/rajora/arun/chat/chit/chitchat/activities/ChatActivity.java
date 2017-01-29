package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.AdapterSidebarContacItem;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactDetailDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.services.SendMessageService;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;
import com.rajora.arun.chat.chit.chitchat.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class ChatActivity extends AppCompatChatListenerActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, AdapterSidebarContacItem.onItemClickListener {

	private static final int REQUEST_PLAY_SERVICES_RESOLUTION = 9000;
	private static final int CURSOR_CONTACT_LIST_LOADER_ID = 300;
	private static final int CURSOR_CONTACT_ITEM_LOADER_ID = 400;
	private String my_ph_no;
	private FirebaseBotsDataModel botData;
	private ContactItemDataModel contactData;
	private TextView chat_name_textview;
	private ImageView chat_image_imageview;
	private AlertDialog.Builder mAlertDialog;
	private CardView chatImageViewHolder;
	private HashSet<Integer> mOpenIndexSet;
	private int mNotUserWarningProgress;
	private RecyclerView mRecyclerView;
	private AdapterSidebarContacItem mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
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
		mNotUserWarningProgress = 0;
		chatImageViewHolder = (CardView) findViewById(R.id.chat_image_container);
		chat_name_textview = (TextView) findViewById(R.id.chat_name);
		chat_image_imageview = (ImageView) findViewById(R.id.chat_image);
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
		} else if (extras != null) {
			restoreValuesFromBundle(extras);
		}
		if (mOpenIndexSet == null) {
			mOpenIndexSet = new HashSet<Integer>();
		}
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
				ActivityOptionsCompat options = ActivityOptionsCompat.
						makeSceneTransitionAnimation(ChatActivity.this,
								(View) chatImageViewHolder, ChatActivity.this.getString(R.string.pic_transition_name));
				startActivity(intent, options.toBundle());
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
		mAdapter = new AdapterSidebarContacItem(this, this, null, ContractContacts._ID, mOpenIndexSet);
		mRecyclerView.setAdapter(mAdapter);

	}

	private void restoreValuesFromBundle(Bundle bundle) {

		if (bundle.getString("type").equals("bot_data_model")) {
			botData = bundle.getParcelable("data");
			chat_name_textview.setText(botData.getName());
			contactData = new ContactItemDataModel(botData.getGid(), true);
			if (botData.getImage_url() != null) {
				Glide.with(this)
						.using(new FirebaseImageLoader())
						.load(FirebaseStorage.getInstance().getReference(botData.getImage_url()))
						.into(chat_image_imageview);
				chat_name_textview.setContentDescription(String.format(getString(R.string.chatbot_cd), botData.getName()));
				chat_image_imageview.setContentDescription(String.format(getString(R.string.pp_chat_bot_cd), botData.getName()));
			}
		} else {
			contactData = bundle.getParcelable("data");
			getSupportLoaderManager().initLoader(CURSOR_CONTACT_ITEM_LOADER_ID, null, this);
		}
		if (bundle.containsKey("openindex")) {
			mOpenIndexSet = (HashSet<Integer>) bundle.getSerializable("openindex");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
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
		if (mOpenIndexSet != null) {
			outState.putSerializable("openindex", mOpenIndexSet);
		}
		outState.putInt("NotUserWarningProgress", mNotUserWarningProgress);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home:
				if (NavUtils.getParentActivityName(this) != null)
					supportFinishAfterTransition();
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
					ContractContacts.COLUMN_IS_BOT + " = ? ", new String[]{"0"},
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
					ContractContacts.COLUMN_CONTACT_ID + " = ? AND " + ContractContacts.COLUMN_IS_BOT + " = ? ",
					new String[]{contactData.getContact_id(), contactData.is_bot() ? "1" : "0"}, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == CURSOR_CONTACT_LIST_LOADER_ID) {
			mAdapter.swapCursor(data);
		} else if (loader.getId() == CURSOR_CONTACT_ITEM_LOADER_ID && Utils.canCursorMoveToFirst(data)) {
			processCursor(data);
		}
	}

	private void processCursor(Cursor cursor) {
		if (Utils.canCursorMoveToFirst(cursor)) {
			ContactDetailDataModel item = new ContactDetailDataModel(cursor);
			if (mNotUserWarningProgress != 2 && mAlertDialog == null) {
				if (item.is_bot && !item.is_user) {
					showBotDeletedDialog();
				} else if (!item.is_bot && !item.is_user) {
					showUserNotActivatedDialog();
				}
			}
			ImageUtils.loadImageIntoView(this, item, chat_image_imageview);
			if (item.name == null || item.name.isEmpty()) {
				chat_name_textview.setText(item.contact_id);
				if (item.is_bot) {
					chat_name_textview.setContentDescription(String.format("%s%s", getString(R.string.cc_cbot_cd), item.contact_id));
					chat_image_imageview.setContentDescription(String.format("%s%s", getString(R.string.cc_cb_p_cd), item.contact_id));
				} else {
					chat_name_textview.setContentDescription(String.format("%s%s", getString(R.string.cc_contact_cd), item.contact_id));
					chat_image_imageview.setContentDescription(String.format("%s%s", getString(R.string.cc_ppic_cd), item.contact_id));
				}
			} else {
				chat_name_textview.setText(item.name);
				if (item.is_bot) {
					chat_name_textview.setContentDescription(String.format("%s%s", getString(R.string.cc_cbot_cd), item.name));
					chat_image_imageview.setContentDescription(String.format("%s%s", getString(R.string.cc_cb_p_cd), item.name));
				} else {
					chat_name_textview.setContentDescription(String.format("%s%s", getString(R.string.cc_contact_cd), item.name));
					chat_image_imageview.setContentDescription(String.format("%s%s", getString(R.string.cc_ppic_cd), item.name));
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
	public void onSendClick(int position, ContactDetailDataModel item, AdapterSidebarContacItem.VH holder) {
		String to_id = item.contact_id;
		SendMessageService.startSendTextMessage(this, "+" + my_ph_no, to_id,
				holder.mMessage.getText().toString(), "text", false, Utils.getCurrentTimestamp());
		holder.mMessage.setText("");
	}

	@Override
	public void onContactClick(int position, AdapterSidebarContacItem.VH holder, Set<Integer> openIndexes) {
		if (openIndexes.contains(position)) {
			openIndexes.remove(position);
			holder.sidebarPanel.setVisibility(View.GONE);
		} else {
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
				new AlertDialog.Builder(this).setMessage(R.string.cc_gpse)
						.setTitle(R.string.cc_dnsupported)
						.setPositiveButton(R.string.cc_ok, new DialogInterface.OnClickListener() {
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

	private void showBotDeletedDialog() {
		mNotUserWarningProgress = 1;
		mAlertDialog = new AlertDialog.Builder(this);
		mAlertDialog.setCancelable(false);
		mAlertDialog.setTitle(R.string.cc_bd_title);
		mAlertDialog.setMessage(R.string.cc_bd_message);
		mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress = 2;
				dialogInterface.dismiss();
			}
		});
		mAlertDialog.setNegativeButton("GO BACK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress = 2;
				dialogInterface.dismiss();
				supportFinishAfterTransition();
			}
		});
		mAlertDialog.create().show();
	}

	private void showUserNotActivatedDialog() {
		mNotUserWarningProgress = 1;
		mAlertDialog = new AlertDialog.Builder(this);
		mAlertDialog.setCancelable(false);
		mAlertDialog.setTitle(R.string.cc_user_nr_title);
		mAlertDialog.setMessage(R.string.cc_user_nr_warning);
		mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress = 2;
				dialogInterface.dismiss();
			}
		});
		mAlertDialog.setNegativeButton("Invite", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress = 2;
				dialogInterface.dismiss();
				Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.cc_invitation_title))
						.setMessage(String.format("%s%s%s", getString(R.string.cc_invitation_message), my_ph_no, getString(R.string.cc_deep_link_url)))
						.setDeepLink(Uri.parse(getString(R.string.cc_deep_link_url)))
						.build();
				startActivityForResult(intent, 1023);
			}
		});
		mAlertDialog.setNeutralButton("GO BACK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mNotUserWarningProgress = 2;
				dialogInterface.dismiss();
				supportFinishAfterTransition();
			}
		});
		mAlertDialog.create().show();
	}
}
