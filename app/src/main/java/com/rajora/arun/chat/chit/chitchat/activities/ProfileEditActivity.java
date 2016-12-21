package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;
import com.rajora.arun.chat.chit.chitchat.services.UploadProfileDetails;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileEditActivity extends AppCompatActivity{

    private ImageView mProfilePicHolder;
    private Button mButtonChangeProfilePic;
    private Button mSubmitDetails;
    private EditText mProfileName;
    private EditText mProfileAbout;
    private Bitmap mProfilePic;

    final static int REQUEST_CAPTURE_IMAGE=1;
    final static int REQUEST_PICK_IMAGE=2;

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
        setContentView(R.layout.activity_profile_edit);

        mProfilePicHolder=(ImageView)findViewById(R.id.profile_pic_holder);
        mButtonChangeProfilePic=(Button)findViewById(R.id.upload_profile_pic_button);
        mSubmitDetails=(Button) findViewById(R.id.finish_button_profile_upload);
        mProfileName=(EditText) findViewById(R.id.profile_name);
        mProfileAbout=(EditText) findViewById(R.id.profile_about);
        mProfilePicHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });
        mButtonChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });

        restoreLayoutValues(savedInstanceState);

        mSubmitDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String profilePicString=null;
                    if(mProfilePic!=null)
                        profilePicString=ImageUtils.bitmapToString(mProfilePic);
                    FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(ProfileEditActivity.this));
                Bundle extrasBundle = new Bundle();
                extrasBundle.putString(UploadProfileDetails.PARAM_NAME, mProfileName.getText().toString());
                extrasBundle.putString(UploadProfileDetails.PARAM_ABOUT, mProfileAbout.getText().toString());
                extrasBundle.putString(UploadProfileDetails.PARAM_PIC, profilePicString);
                Job myJob = dispatcher.newJobBuilder()
                        .setService(UploadProfileDetails.class)
                        .setTag("profile-upload")
                        .setRecurring(false)
                        .setLifetime(Lifetime.FOREVER)
                        .setTrigger(Trigger.NOW)
                        .setReplaceCurrent(false)
                        .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .setExtras(extrasBundle)
                        .build();
                dispatcher.mustSchedule(myJob);
                ProfileEditActivity.this.finish();
            }
        });
    }
    private void restoreLayoutValues(Bundle savedInstanceState){
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
        if(savedInstanceState!=null && savedInstanceState.containsKey("name")){
            mProfileName.setText(savedInstanceState.getString("name"));
        }
        else{
            mProfileName.setText(sharedPreferences.getString("name",""));
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey("about")){
            mProfileAbout.setText(savedInstanceState.getString("about"));
        }
        else{
            mProfileAbout.setText(sharedPreferences.getString("about",""));
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey("profile_pic")){
            mProfilePic=ImageUtils.stringToBitmap(savedInstanceState.getString("name"));
        }
        else{
            String imageString=sharedPreferences.getString("pic",null);
            if(imageString!=null && imageString.length()!=0)
                mProfilePicHolder.setImageBitmap(ImageUtils.stringToBitmap(imageString));
        }
    }
    private void changeProfilePic(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_image_from)
        .setItems(R.array.pick_image_option_list_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent,REQUEST_CAPTURE_IMAGE);
                    }
                    else{
                        Toast.makeText(ProfileEditActivity.this,"No Camera App found !",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(which==1){
                    Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
                }
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mProfilePic = (Bitmap) extras.get("data");
            mProfilePicHolder.setImageBitmap(mProfilePic);
        }
        else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if(bitmap!=null){
                    bitmap=ThumbnailUtils.extractThumbnail(bitmap,512,512);
                    mProfilePic=bitmap;
                    mProfilePicHolder.setImageBitmap(mProfilePic);
                }
            } catch (IOException e) {
                e.printStackTrace();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mProfileName!=null)
        outState.putString("name",mProfileName.getText().toString());
        if(mProfileAbout!=null)
            outState.putString("about",mProfileAbout.getText().toString());
        if(mProfilePic!=null)
            outState.putString("profile_pic", ImageUtils.bitmapToString(mProfilePic));
    }
}
