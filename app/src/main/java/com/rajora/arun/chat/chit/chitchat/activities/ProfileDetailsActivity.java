package com.rajora.arun.chat.chit.chitchat.activities;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactDetailDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactItemDataModel;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

public class ProfileDetailsActivity extends AppCompatChatListenerActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    ImageView image;
    TextView textView1;
    TextView textView2;
    TextView textView3;

    private static final int CURSOR_LOADER_ID=5;

    FirebaseBotsDataModel botData;
    ContactItemDataModel contactData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        image= ((ImageView) findViewById(R.id.details_image));
        textView1= (TextView) findViewById(R.id.details_text1);
        textView2= (TextView) findViewById(R.id.details_text2);
        textView3= (TextView) findViewById(R.id.details_text3);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            fillLayoutFromBundle(extras);
        }
        else if(savedInstanceState!=null){
            fillLayoutFromBundle(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(botData!=null){
            outState.putString("type","bot_data_model");
            outState.putParcelable("data",botData);
        }
        else{
            outState.putString("type","contact_data_model");
            outState.putParcelable("data",contactData);
        }
    }

    private void fillLayoutFromBundle(Bundle bundle){

        if(bundle.getString("type").equals("bot_data_model")){
            botData=bundle.getParcelable("data");
            textView1.setText(botData.getName());
            textView2.setText(botData.getDesc());
            textView3.setText("Developed By : "+botData.getDev_name());
            textView1.setContentDescription("bot name is "+botData.getName());
            textView2.setContentDescription("about the bot : "+botData.getDesc());
            textView3.setContentDescription("developed by : "+botData.getDev_name());
            if(botData.getImage_url()!=null){
	            ImageUtils.loadBitmapFromFirebase(this,botData.getImage_url(),R.drawable.empty_profile_pic,image);
            }
        }
        else{
            contactData=bundle.getParcelable("data");
            getSupportLoaderManager().initLoader(CURSOR_LOADER_ID,null,this);
        }
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
			    ContractContacts.COLUMN_CONTACT_ID+" = ? AND "+ContractContacts.COLUMN_IS_BOT+" = ? ",
			    new String[]{contactData.getContact_id(),contactData.is_bot()?"1":"0"},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(utils.canCursorMoveToFirst(data)) {

	        ContactDetailDataModel item = new ContactDetailDataModel(data);
	        ImageUtils.loadImageIntoView(this,item,image);
	        if (item.is_bot) {
		        textView1.setContentDescription("bot name is " + (item.name == null ? "" : item.name));
		        textView2.setContentDescription("about the bot : " + (item.about == null ? "" : item.about));
		        textView3.setContentDescription("developed by : " + (item.dev_name == null ? "" : item.dev_name));
	        } else {
		        textView1.setContentDescription("contact name is " + (item.name == null ? "" : item.name));
		        textView2.setContentDescription("about : " + (item.about == null ? "" : item.about));
		        textView3.setContentDescription("contact number is " + item.contact_id);
	        }
	        textView1.setText(item.name == null ? "" : item.name);
	        textView2.setText(item.about == null ? "" : item.about);
	        textView3.setText(item.is_bot ? ("Developed by : " + item.dev_name) : item.contact_id);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
