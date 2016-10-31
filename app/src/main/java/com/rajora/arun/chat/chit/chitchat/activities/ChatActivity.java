package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_sidebar_contact_item;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_bots;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;
import com.rajora.arun.chat.chit.chitchat.services.SendMessageService;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

public class ChatActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,adapter_sidebar_contact_item.onItemClickListener{

    String type;
    byte[] img;
    String name;
    String number;
    String about;
    String Gid;
    String image_url;
    String dev_name;
    boolean is_bot;
    Cursor mcursorAbout;
    private String my_ph_no;
    private FirebaseStorage firebaseStorage;

    private RecyclerView mRecyclerView;
    private adapter_sidebar_contact_item mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final int CURSOR_LOADER_ID=300;

    FetchNewChatData mBoundService;
    boolean service_connected=false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service_connected=false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            service_connected=true;
            FetchNewChatData.customBinder myBinder = (FetchNewChatData.customBinder) service;
            mBoundService = myBinder.getService();
            mBoundService.setCurrentItemId(is_bot?Gid:number);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.onBackPressed();
            }
        });
        findViewById(R.id.chat_image_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number!=null && number.length()>0){
                    if(type.equals("contact")){

                    }
                }
            }
        });
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            type=extras.getString("type","");
            if(type.equals("contact")){
                is_bot=false;
                img=extras.getByteArray("profilePic");
                name=extras.getString("name","");
                number=extras.getString("number","");
                about=extras.getString("about","");
                mcursorAbout=getContentResolver().query(ChatContentProvider.CONTACTS_URI,new String[]{
                        contract_contacts.COLUMN_PIC,
                        contract_contacts.COLUMN_PIC_TIMESTAMP,
                        contract_contacts.COLUMN_ABOUT,
                        contract_contacts.COLUMN_IS_USER,
                        contract_contacts.COLUMN_NAME,
                        contract_contacts.COLUMN_PH_NUMBER,
                        contract_contacts.COLUMN_PIC_URL},
                    contract_contacts.COLUMN_PH_NUMBER+" = ? ",new String[]{number},null);
                if(img!=null && img.length>0){
                    ((ImageView) findViewById(R.id.chat_image)).setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                }
                ((TextView) findViewById(R.id.chat_name)).setText((name==null || name.length()<=0)?number:name);
                findViewById(R.id.chat_name).setContentDescription("Chatting with "+((name==null || name.length()<=0)?number:name));
                findViewById(R.id.chat_image).setContentDescription("Profile Picture of "+((name==null || name.length()<=0)?number:name));
            }
            else if(type.equals("bot")){
                is_bot=true;
                name=extras.getString("name","");
                number=extras.getString("dev_no","");
                about=extras.getString("about","");
                Gid=extras.getString("Gid","");
                image_url=extras.getString("imageurl","");
                dev_name=extras.getString("dev_name","");
                if(image_url!=null){
                    firebaseStorage= FirebaseStorage.getInstance();
                    Glide.with(this)
                            .using(new FirebaseImageLoader())
                            .load(firebaseStorage.getReference(image_url))
                            .into(((ImageView) findViewById(R.id.chat_image)));
                }
                ((TextView) findViewById(R.id.chat_name)).setText((name==null || name.length()<=0)?number:name);
                findViewById(R.id.chat_name).setContentDescription("Chatting with bot "+((name==null || name.length()<=0)?number:name));
                findViewById(R.id.chat_image).setContentDescription("Profile Picture of bot "+((name==null || name.length()<=0)?number:name));
            }

        }
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        my_ph_no=sharedPreferences.getString("phone","");

        mRecyclerView = (RecyclerView)findViewById(R.id.sidebar_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        mAdapter=new adapter_sidebar_contact_item(this,this,null, contract_contacts.COLUMN_PH_NUMBER);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, FetchNewChatData.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        if(service_connected )
            unbindService(mServiceConnection);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mcursorAbout!=null && !mcursorAbout.isClosed())
            mcursorAbout.close();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(this)!=null)
                    finish();
                else
                    NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_upload:
                return true;
            case R.id.action_about:
                Intent about_intent=new Intent(this,AboutActivity.class);
                startActivity(about_intent);
                return true;
            case R.id.action_Update_Profile:
                Intent update_intent=new Intent(this,ProfileEditActivity.class);
                startActivity(update_intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,ChatContentProvider.CONTACTS_URI,
                new String[]{
                        contract_contacts.COLUMN_NAME,
                        contract_contacts.COLUMN_PIC,
                        contract_contacts.COLUMN_PIC_URL,
                        contract_contacts.COLUMN_PH_NUMBER,
                        contract_contacts.COLUMN_IS_USER,
                        contract_contacts.COLUMN_ABOUT,
                        contract_contacts.COLUMN_PIC_TIMESTAMP},
                null,null,contract_contacts.COLUMN_NAME+" COLLATE NOCASE ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }


    @Override
    public void onSendClick(int position, Cursor cursor, adapter_sidebar_contact_item.VH holder) {
        String to_id=cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_PH_NUMBER));
        SendMessageService.startSendTextMessageToUser(this,"+"+my_ph_no,to_id,"text",holder.mMessage.getText().toString(), utils.getCurrentTimestamp());
        holder.mMessage.setText("");
    }

    @Override
    public void onContactClick(int position, adapter_sidebar_contact_item.VH holder) {
        holder.sidebarPanel.setVisibility(holder.sidebarPanel.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
    }
}
