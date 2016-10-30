package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.fragments.fragment_bot_list;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;

public class ProfileDetailsActivity extends AppCompatActivity {

    ImageView image;
    TextView textView1;
    TextView textView2;
    TextView textView3;

    private FirebaseStorage firebaseStorage;

    byte[] img;
    String text1;
    String text2;
    String text3;
    String type;
    String imgUrl;

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
            mBoundService.setCurrentItemId(null);
        }

    };

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
            type=extras.getString("type","");
            if(type.equals("contact") || type.equals("chats")){
                img=extras.getByteArray("image");
                text1=extras.getString("text1","");
                text2=extras.getString("text2","");
                text3=extras.getString("text3","");

                if(img!=null && img.length>0)
                    image.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                textView1.setText(text1);
                textView2.setText(text2);
                textView3.setText(text3);

            }
            else if(type.equals("bot")){
                imgUrl=extras.getString("imageurl");
                text1=extras.getString("text1","");
                text2=extras.getString("text2","");
                text3=extras.getString("text3","");

                firebaseStorage= FirebaseStorage.getInstance();
                if(imgUrl!=null){
                    Glide.with(this)
                            .using(new FirebaseImageLoader())
                            .load(firebaseStorage.getReference(imgUrl))
                            .into(image);
                }
                textView1.setText(text1);
                textView2.setText(text2);
                textView3.setText(text3);
            }

        }

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
}
