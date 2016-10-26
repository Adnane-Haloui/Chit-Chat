package com.rajora.arun.chat.chit.chitchatdevelopers.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
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

import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.services.UploadBotDetails;
import com.rajora.arun.chat.chit.chitchatdevelopers.services.UploadProfileDetails;
import com.rajora.arun.chat.chit.chitchatdevelopers.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BotEditActivity extends AppCompatActivity {
    private ImageView mBotPicHolder;
    private Button mButtonChangeBotPic;
    private ImageButton mSubmitDetails;
    private EditText mBotName;
    private EditText mBotAbout;
    private EditText mBotUrl;
    private EditText mBotSecret;
    private TextInputLayout mNameTextInputLayout;
    private Bitmap mBotPic;
    private String mName;
    private String mAbout;
    private String mUrl;
    private String mSecret;

    final static int REQUEST_CAPTURE_IMAGE=1;
    final static int REQUEST_PICK_IMAGE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_edit);
        mBotPicHolder=(ImageView)findViewById(R.id.bot_pic_holder);
        mButtonChangeBotPic=(Button)findViewById(R.id.upload_bot_pic_button);
        mSubmitDetails=(ImageButton) findViewById(R.id.finish_button_bot_upload);
        mBotName=(EditText) findViewById(R.id.bot_name);
        mBotAbout=(EditText) findViewById(R.id.bot_about);
        mBotUrl=(EditText)findViewById(R.id.bot_link);
        mBotSecret=(EditText)findViewById(R.id.bot_secret);
        mNameTextInputLayout=(TextInputLayout)findViewById(R.id.bot_name_textInputLayout);
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
                if(mBotName.getText().toString()!=null && mBotName.getText().toString().length()>0){
                    mName=mBotName.getText().toString();
                    mAbout=mBotAbout.getText().toString();
                    mUrl=mBotUrl.getText().toString();
                    mSecret=mBotSecret.getText().toString();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if(mBotPic!=null)
                        mBotPic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadBotDetails.startUploadBot(BotEditActivity.this,mUrl,data,mName,mAbout,mSecret);
                    BotEditActivity.this.finish();
                }
                else{
                    mNameTextInputLayout.setError("Required!");
                }
            }
        });
        restoreLayoutValues(savedInstanceState);
    }
    private void restoreLayoutValues(Bundle savedInstanceState){

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
                                Toast.makeText(BotEditActivity.this,"No Camera App found !",Toast.LENGTH_SHORT).show();
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
            mBotPic = (Bitmap) extras.get("data");
            mBotPicHolder.setImageBitmap(mBotPic);
        }
        else if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if(bitmap!=null){
                    bitmap= ThumbnailUtils.extractThumbnail(bitmap,512,512);
                    mBotPic=bitmap;
                    mBotPicHolder.setImageBitmap(mBotPic);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
