package com.rajora.arun.chat.chit.chitchat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.services.UploadProfileDetails;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileEditActivity extends AppCompatChatListenerActivity{

    private ImageView mProfilePicHolder;
    private EditText mProfileName;
    private EditText mProfileAbout;
    private Button mChangeProfilePicButton;

    private String mProfilePicUri;
	private String mCurrentPhotoPath;

	final static int REQUEST_CAPTURE_IMAGE=1;
    final static int REQUEST_PICK_IMAGE=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        mProfilePicHolder=(ImageView)findViewById(R.id.profile_pic_holder);
        mProfileName=(EditText) findViewById(R.id.profile_name);
        mProfileAbout=(EditText) findViewById(R.id.profile_about);
	    mChangeProfilePicButton=(Button)findViewById(R.id.upload_profile_pic_button);
	    restoreLayoutValues(savedInstanceState);
	    mProfilePicHolder.setOnClickListener(new View.OnClickListener() {

		    @Override
		    public void onClick(View v) {
			    changeProfilePic();
		    }
	    });
	    mChangeProfilePicButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    changeProfilePic();
		    }
	    });

	    findViewById(R.id.cancel_button_profile_upload).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    finish();
		    }
	    });
        findViewById(R.id.finish_button_profile_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizeProfileDetails();
            }
        });
    }

	private void finalizeProfileDetails(){
		getSharedPreferences("user-details",MODE_PRIVATE).edit().putString("profile-pic",mProfilePicUri).commit();
		Intent uploadProfileIntent=new Intent(this,UploadProfileDetails.class);
		uploadProfileIntent.putExtra(UploadProfileDetails.PARAM_NAME,mProfileName.getText().toString());
		uploadProfileIntent.putExtra(UploadProfileDetails.PARAM_ABOUT,mProfileAbout.getText().toString());
		uploadProfileIntent.putExtra(UploadProfileDetails.PARAM_PIC_URI,mProfilePicUri);
		startService(uploadProfileIntent);
		finish();
	}

    private void restoreLayoutValues(Bundle savedInstanceState){
        SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
	    if(savedInstanceState!=null && savedInstanceState.containsKey("profile_pic")){
		    mProfilePicUri=savedInstanceState.getString("profile_pic");
		    if(mProfilePicUri!=null){
			    mProfilePicHolder.setImageURI(Uri.parse(mProfilePicUri));
		    }
	    }
	    else{
		    String mPicBase64=sharedPreferences.getString("profile_pic",null);
		    if(mPicBase64!=null && mPicBase64.length()>=10)
			    mProfilePicHolder.setImageBitmap(ImageUtils.stringToBitmap(mPicBase64));
	    }
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
	    if(savedInstanceState!=null && savedInstanceState.containsKey("profile_pic_current_path")){
		    mCurrentPhotoPath=savedInstanceState.getString("profile_pic_current_path");
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
	                File photoFile = createImageFile();
	                if (photoFile == null) {
		                Toast.makeText(ProfileEditActivity.this,"Error creating file for image!",Toast.LENGTH_SHORT).show();
	                }
	                else{
		                Uri photoURI = FileProvider.getUriForFile(ProfileEditActivity.this,
				                "com.rajora.arun.chat.chit.chitchat.fileprovider",
				                photoFile);
		                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
		                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			                startActivityForResult(takePictureIntent,REQUEST_CAPTURE_IMAGE);
		                }
		                else{
			                Toast.makeText(ProfileEditActivity.this,"No camera app found!",Toast.LENGTH_SHORT).show();
		                }
	                }
                }
                else if(which==1){
                    Intent PickImageintent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    if (PickImageintent.resolveActivity(getPackageManager()) != null) {
		                startActivityForResult(PickImageintent,REQUEST_PICK_IMAGE);
	                }
	                else{
		                Toast.makeText(ProfileEditActivity.this,"No app found to pick image!",Toast.LENGTH_SHORT).show();
	                }
                }
            }
        });
        builder.create().show();
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK){
			if(requestCode==REQUEST_CAPTURE_IMAGE){
				mProfilePicUri = Uri.fromFile(new File(mCurrentPhotoPath)).toString();
				mProfilePicHolder.setImageURI(Uri.parse(mProfilePicUri));
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(Uri.parse(mProfilePicUri));
				sendBroadcast(mediaScanIntent);
			}
			else if(requestCode==REQUEST_PICK_IMAGE){
				mProfilePicUri = data.getData().toString();
				mProfilePicHolder.setImageURI(data.getData());
			}

		}
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences("user-details",MODE_PRIVATE).edit()
		        .putBoolean("first_profile_edit",true).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mProfileName!=null)
        outState.putString("name",mProfileName.getText().toString());
        if(mProfileAbout!=null)
            outState.putString("about",mProfileAbout.getText().toString());
        if(mProfilePicUri!=null)
            outState.putString("profile_pic",mProfilePicUri);
	    if(mCurrentPhotoPath!=null)
		    outState.putString("profile_pic_current_path",mCurrentPhotoPath);
    }


	private File createImageFile(){
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+Math.floor(Math.random()*1000);
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = null;
		try {
			image = File.createTempFile(imageFileName, ".jpg", storageDir);
			mCurrentPhotoPath = image.getAbsolutePath();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}