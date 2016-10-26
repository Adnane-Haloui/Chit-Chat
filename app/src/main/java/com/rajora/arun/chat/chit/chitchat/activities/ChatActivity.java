package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_bots;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

public class ChatActivity extends AppCompatActivity {

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
    private FirebaseStorage firebaseStorage;

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

            }

        }




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
}
