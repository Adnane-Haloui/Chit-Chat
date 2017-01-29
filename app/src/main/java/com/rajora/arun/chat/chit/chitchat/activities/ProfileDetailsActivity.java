package com.rajora.arun.chat.chit.chitchat.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.R.drawable;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactDetailDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;
import com.rajora.arun.chat.chit.chitchat.utils.Utils;

public class ProfileDetailsActivity extends AppCompatChatListenerActivity
		implements LoaderCallbacks<Cursor> {

	private static final int CURSOR_LOADER_ID = 5;
	ImageView image;
	TextView textView1;
	TextView textView2;
	TextView textView3;
	FirebaseBotsDataModel botData;
	ContactItemDataModel contactData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_details);
		image = (ImageView) findViewById(id.details_image);
		textView1 = (TextView) findViewById(id.details_text1);
		textView2 = (TextView) findViewById(id.details_text2);
		textView3 = (TextView) findViewById(id.details_text3);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			fillLayoutFromBundle(extras);
		} else if (savedInstanceState != null) {
			fillLayoutFromBundle(savedInstanceState);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (botData != null) {
			outState.putString("type", "bot_data_model");
			outState.putParcelable("data", botData);
		} else {
			outState.putString("type", "contact_data_model");
			outState.putParcelable("data", contactData);
		}
	}

	private void fillLayoutFromBundle(Bundle bundle) {

		if (bundle.getString("type").equals("bot_data_model")) {
			botData = bundle.getParcelable("data");
			textView1.setText(botData.getName());
			textView2.setText(botData.getDesc());
			textView3.setText(String.format("%s%s", getString(R.string.cc_developed_by), botData.getDev_name()));
			textView1.setContentDescription(String.format("%s%s", getString(R.string.cc_bot_name_is), botData.getName()));
			textView2.setContentDescription(String.format("%s%s", getString(R.string.cc_about_bot), botData.getDesc()));
			textView3.setContentDescription(String.format("%s%s", getString(R.string.cc_deve_by), botData.getDev_name()));
			ImageUtils.loadBitmapFromFirebase(this, "/botItem/" + botData.getGid() + "/botpic.png", R.drawable.empty_profile_pic, image);
		} else {
			contactData = bundle.getParcelable("data");
			getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				supportFinishAfterTransition();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, ChatContentProvider.CONTACT_LIST_URI,
				new String[]{
						ContractContacts.COLUMN_CONTACT_ID,
						ContractContacts.COLUMN_IS_BOT,
						ContractContacts.COLUMN_NAME,
						ContractContacts.COLUMN_ABOUT,
						ContractContacts.COLUMN_IS_USER,
						ContractContacts.COLUMN_DEV_NAME,
						ContractContacts.COLUMN_PIC_URL,
						ContractContacts.COLUMN_PIC_URI},
				ContractContacts.COLUMN_CONTACT_ID + " = ? AND " + ContractContacts.COLUMN_IS_BOT + " = ? ",
				new String[]{contactData.getContact_id(), contactData.is_bot() ? "1" : "0"}, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (Utils.canCursorMoveToFirst(data)) {

			ContactDetailDataModel item = new ContactDetailDataModel(data);
			ImageUtils.loadImageIntoView(this, item, image);
			if (item.is_bot) {
				textView1.setContentDescription(String.format("%s%s", getString(R.string.cc_bot_name_is), item.name));
				textView2.setContentDescription(String.format("%s%s", getString(R.string.cc_about_bot), item.about));
				textView3.setContentDescription(String.format("%s%s", getString(R.string.cc_deve_by), item.dev_name));
				ImageUtils.loadBitmapFromFirebase(this, "/botItem/" + item.contact_id + "/botpic.png", R.drawable.empty_profile_pic, image);
			} else {
				textView1.setContentDescription(String.format("%s%s", getString(R.string.cc_con_name_is), item.name == null ? "" : item.name));
				textView2.setContentDescription(String.format("%s%s", getString(R.string.cc_contact_about_is), item.about == null ? "" : item.about));
				textView3.setContentDescription(String.format("%s%s", getString(R.string.cc_contact_no_is), item.contact_id));
				ImageUtils.loadImageIntoView(this, item, image);
			}
			textView1.setText(item.name == null ? "" : item.name);
			textView2.setText(item.about == null ? "" : item.about);
			textView3.setText(item.is_bot ? String.format("%s%s", getString(R.string.cc_developed_by_dev), item.dev_name) : item.contact_id);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
