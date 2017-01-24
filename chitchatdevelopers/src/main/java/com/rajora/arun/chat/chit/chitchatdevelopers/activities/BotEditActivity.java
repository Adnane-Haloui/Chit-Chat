package com.rajora.arun.chat.chit.chitchatdevelopers.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotContentProvider;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;
import com.rajora.arun.chat.chit.chitchatdevelopers.services.UploadBotDetails;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	final static int REQUEST_CAPTURE_IMAGE=1;
	final static int REQUEST_PICK_IMAGE=2;

	private static final int CURSOR_LOADER_ID=101;

	private String Gid;
	private String id;
	private String mProfilePicUri;
	private String mCurrentPhotoPath;
	int mRequestCode;
	String dataUri;

	private EditText mBotName;
    private EditText mBotAbout;
    private EditText mBotUrl;
    private EditText mBotSecret;
    private TextInputLayout mNameTextInputLayout;
	private TextInputLayout mEndpointTextInputLayout;
	private TextInputLayout mAboutTextInputLayout;
	ImageView mBotPicHolder;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_edit);

		mBotPicHolder = (ImageView) findViewById(R.id.bot_pic_holder);
		Button mButtonChangeBotPic = (Button) findViewById(R.id.upload_bot_pic_button);
		ImageButton mSubmitDetails = (ImageButton) findViewById(R.id.finish_button_bot_upload);
		mBotName=(EditText) findViewById(R.id.bot_name);
		mBotAbout=(EditText) findViewById(R.id.bot_about);
		mBotUrl=(EditText)findViewById(R.id.bot_link);
		mBotSecret=(EditText)findViewById(R.id.bot_secret);
		mNameTextInputLayout=(TextInputLayout)findViewById(R.id.bot_name_textInputLayout);
		mEndpointTextInputLayout=(TextInputLayout)findViewById(R.id.bot_endpoint_textInputLayout);
		mAboutTextInputLayout=(TextInputLayout)findViewById(R.id.bot_about_textInputLayout);
		if(savedInstanceState!=null)
	    {
		    restoreLayoutValues(savedInstanceState);
		    mRequestCode=savedInstanceState.getInt("requestCode");
		    if(savedInstanceState.containsKey("datauri"))
			    dataUri=savedInstanceState.getString("datauri");
	    }
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("data")){
			Gid=getIntent().getStringExtra("data");
	        id=getIntent().getStringExtra("id");
        }
	    else if(savedInstanceState!=null && savedInstanceState.containsKey("data")){
	        Gid=savedInstanceState.getString("data");
        }
	    if(Gid!=null){
		    getSupportLoaderManager().initLoader(CURSOR_LOADER_ID,null,this);
	    }

        mBotPicHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });
        mButtonChangeBotPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });
        mSubmitDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBotName.getText()!=null && !mBotName.getText().toString().isEmpty() &&
		                mBotAbout.getText()!=null && !mBotAbout.getText().toString().isEmpty() &&
			            mBotUrl.getText()!=null && !mBotUrl.getText().toString().isEmpty()){
                    String  name=mBotName.getText().toString();
	                String about=mBotAbout.getText().toString();
                    String url=mBotUrl.getText().toString();
                    String secret=mBotSecret.getText().toString();
	                if(Gid==null){
                    UploadBotDetails.startUploadBot(BotEditActivity.this,url,mProfilePicUri,name,about,secret);
	                }
	                else{
		                if(id==null){
			                Toast.makeText(BotEditActivity.this, R.string.wait_for_data_to_load,Toast.LENGTH_SHORT).show();
		                }
		                else{
			                UploadBotDetails.startUpdateBot(BotEditActivity.this,Gid,id,url,mProfilePicUri,name,about,secret);
		                }
	                }
                    finish();
                }
                else{
	                if(mBotName.getText()==null || mBotName.getText().toString().isEmpty()){
		                mNameTextInputLayout.setError("Required!");
	                }
	                if(mBotAbout.getText()==null || mBotAbout.getText().toString().isEmpty()){
		                mAboutTextInputLayout.setError("Required!");
	                }
	                if(mBotUrl.getText()==null || mBotUrl.getText().toString().isEmpty()){
		                mEndpointTextInputLayout.setError("Required!");
	                }
                }
            }
        });
    }
    private void restoreLayoutValues(Bundle savedInstanceState){
	    if(savedInstanceState.containsKey("name"))
		    mBotName.setText(savedInstanceState.getString("name"));
	    if(savedInstanceState.containsKey("about"))
		    mBotAbout.setText(savedInstanceState.getString("about"));
	    if(savedInstanceState.containsKey("name"))
		    mBotUrl.setText(savedInstanceState.getString("url"));
	    if(savedInstanceState.containsKey("name"))
		    mBotSecret.setText(savedInstanceState.getString("secret"));

	    if(savedInstanceState.containsKey("profile_pic")){
		    mProfilePicUri=savedInstanceState.getString("profile_pic");
		    if(mProfilePicUri!=null){
			    mBotPicHolder.setImageURI(Uri.parse(mProfilePicUri));
		    }
		    else{
			    mBotPicHolder.setImageResource(R.drawable.empty_profile_pic);
		    }
	    }
	    if(savedInstanceState.containsKey("profile_pic_current_path")){
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
								Toast.makeText(BotEditActivity.this,R.string.error_creating_file,Toast.LENGTH_SHORT).show();
							}
							else{
								Uri photoURI = FileProvider.getUriForFile(BotEditActivity.this,
										"com.rajora.arun.chat.chit.chitchatdevelopers.fileprovider",
										photoFile);
								takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
								if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
									startActivityForResult(takePictureIntent,REQUEST_CAPTURE_IMAGE);
								}
								else{
									Toast.makeText(BotEditActivity.this,R.string.no_camera_found,Toast.LENGTH_SHORT).show();
								}
							}
						}
						else if(which==1){
							Intent PickImageintent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
							if (PickImageintent.resolveActivity(getPackageManager()) != null) {
								startActivityForResult(PickImageintent,REQUEST_PICK_IMAGE);
							}
							else{
								Toast.makeText(BotEditActivity.this,R.string.no_img_picker_found,Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
		builder.create().show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			if(mRequestCode==REQUEST_CAPTURE_IMAGE){
				mProfilePicUri = Uri.fromFile(new File(mCurrentPhotoPath)).toString();
				mBotPicHolder.setImageURI(Uri.parse(mProfilePicUri));
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(Uri.parse(mProfilePicUri)));
			}
			else if(mRequestCode==REQUEST_PICK_IMAGE){
				mProfilePicUri = dataUri;
				mBotPicHolder.setImageURI(Uri.parse(dataUri));
			}
			else{
				changeProfilePic();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK){
			mRequestCode=requestCode;
			dataUri=mRequestCode==REQUEST_PICK_IMAGE?data.getData().toString():null;
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
					|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE} ,550);
			}
			else{
				if(requestCode==REQUEST_CAPTURE_IMAGE){
					mProfilePicUri = Uri.fromFile(new File(mCurrentPhotoPath)).toString();
					mBotPicHolder.setImageURI(Uri.parse(mProfilePicUri));
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(Uri.parse(mProfilePicUri)));
				}
				else if(requestCode==REQUEST_PICK_IMAGE){
					mProfilePicUri = data.getData().toString();
					mBotPicHolder.setImageURI(data.getData());
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mBotName.getText()!=null && !mBotName.getText().toString().isEmpty())
			outState.putString("name",mBotName.getText().toString());
		if(mBotAbout.getText()!=null && !mBotAbout.getText().toString().isEmpty())
			outState.putString("about",mBotAbout.getText().toString());
		if(mBotUrl.getText()!=null && !mBotUrl.getText().toString().isEmpty())
			outState.putString("url",mBotUrl.getText().toString());
		if(mBotSecret.getText()!=null && !mBotSecret.getText().toString().isEmpty())
			outState.putString("secret",mBotSecret.getText().toString());
		if(mProfilePicUri!=null)
			outState.putString("profile_pic",mProfilePicUri);
		if(mCurrentPhotoPath!=null)
			outState.putString("profile_pic_current_path",mCurrentPhotoPath);
		outState.putInt("requestCode",mRequestCode);
		if(dataUri!=null)
			outState.putString("datauri",dataUri);
		if(Gid!=null){
			outState.putString("data",Gid);
		}
	}

	private File createImageFile(){
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+Math.floor(Math.random()*1000);
		String imageFileName = "JPEG_" + timeStamp + "_.jpg";
		File image =new  File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),imageFileName);
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, BotContentProvider.CONTENT_URI,new String[]{
				BotContracts.COLUMN_GLOBAL_ID,
				BotContracts.COLUMN_ID,
				BotContracts.COLUMN_BOT_NAME,
				BotContracts.COLUMN_ABOUT,
				BotContracts.COLUMN_API_ENDPOINT,
				BotContracts.COLUMN_SECRET,
				BotContracts.COLUMN_PIC_URI
		},BotContracts.COLUMN_GLOBAL_ID+" = ? ",new String[]{Gid},null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(data!=null && data.moveToFirst()){
			if(mBotName.getText()==null || mBotName.getText().toString().isEmpty()){
				mBotName.setText(data.getString(data.getColumnIndex(BotContracts.COLUMN_BOT_NAME)));
			}
			if(mBotAbout.getText()==null || mBotAbout.getText().toString().isEmpty()){
				mBotAbout.setText(data.getString(data.getColumnIndex(BotContracts.COLUMN_ABOUT)));
			}
			if(mBotUrl.getText()==null || mBotUrl.getText().toString().isEmpty()){
				mBotUrl.setText(data.getString(data.getColumnIndex(BotContracts.COLUMN_API_ENDPOINT)));
			}
			if(mBotSecret.getText()==null || mBotSecret.getText().toString().isEmpty()){
				mBotSecret.setText(data.getString(data.getColumnIndex(BotContracts.COLUMN_SECRET)));
			}
			if(mProfilePicUri==null || mProfilePicUri.isEmpty()){
				if(!data.isNull(data.getColumnIndex(BotContracts.COLUMN_PIC_URI))){
					Glide.with(this)
							.load(Uri.parse(data.getString(data.getColumnIndex(BotContracts.COLUMN_PIC_URI))))
							.fitCenter()
							.diskCacheStrategy(DiskCacheStrategy.RESULT)
							.into(mBotPicHolder);
				}
				else{
					mBotPicHolder.setImageResource(R.drawable.empty_profile_pic);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
