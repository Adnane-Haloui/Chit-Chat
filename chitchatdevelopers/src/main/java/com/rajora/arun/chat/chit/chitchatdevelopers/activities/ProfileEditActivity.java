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
import com.rajora.arun.chat.chit.chitchatdevelopers.services.UploadProfileDetails;
import com.rajora.arun.chat.chit.chitchatdevelopers.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageView mProfilePicHolder;
    private Button mButtonChangeProfilePic;
    private ImageButton mSubmitDetails;
    private EditText mProfileName;
    private EditText mProfileAbout;
    private TextInputLayout mNameTextInputLayout;
    private Bitmap mProfilePic;
    private String mName;
    private String mAbout;

    final static int REQUEST_CAPTURE_IMAGE=1;
    final static int REQUEST_PICK_IMAGE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        mProfilePicHolder=(ImageView)findViewById(R.id.profile_pic_holder);
        mButtonChangeProfilePic=(Button)findViewById(R.id.upload_profile_pic_button);
        mSubmitDetails=(ImageButton) findViewById(R.id.finish_button_profile_upload);
        mProfileName=(EditText) findViewById(R.id.profile_name);
        mProfileAbout=(EditText) findViewById(R.id.profile_about);
        mNameTextInputLayout=(TextInputLayout)findViewById(R.id.name_textInputLayout);
        mProfileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(count==0 && mSubmitDetails.getVisibility()!= View.GONE){
                    mSubmitDetails.setVisibility(View.GONE);
                }
                else if(count!=0 && mSubmitDetails.getVisibility()!=View.VISIBLE){
                    mSubmitDetails.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && mSubmitDetails.getVisibility()!= View.GONE){
                    mSubmitDetails.setVisibility(View.GONE);
                }
                else if(count!=0 && mSubmitDetails.getVisibility()!=View.VISIBLE){
                    mSubmitDetails.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0 && mSubmitDetails.getVisibility()!= View.GONE){
                    mSubmitDetails.setVisibility(View.GONE);
                }
                else if(s.length()!=0 && mSubmitDetails.getVisibility()!=View.VISIBLE){
                    mSubmitDetails.setVisibility(View.VISIBLE);
                }
            }
        });
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
        mSubmitDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProfileName.getText().toString()!=null && mProfileName.getText().toString().length()>0){
                    mName=mProfileName.getText().toString();
                    mAbout=mProfileAbout.getText().toString();
                    SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
                    final String ph_no=sharedPreferences.getString("phone","null");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if(mProfilePic!=null)
                        mProfilePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    SharedPreferences preferences=getSharedPreferences("user-details",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("failed","0");
                    editor.commit();
                    UploadProfileDetails.startUploadProfile(ProfileEditActivity.this,ph_no,data,mName,mAbout);
                    ProfileEditActivity.this.finish();
                }
                else{
                    mNameTextInputLayout.setError("Required!");
                }
            }
        });
        restoreLayoutValues(savedInstanceState);
    }
    private void restoreLayoutValues(Bundle savedInstanceState){
        if(true){
            SharedPreferences sharedPreferences=getSharedPreferences("user-details",MODE_PRIVATE);
            String imageString=sharedPreferences.getString("pic",null);
            if(imageString!=null && imageString.length()!=0)
                mProfilePicHolder.setImageBitmap(ImageUtils.stringToBitmap(imageString));
            mProfileName.setText(sharedPreferences.getString("name",""));
            mProfileAbout.setText(sharedPreferences.getString("about",""));
            if(mProfileName.getText().toString().length()!=0)
                mSubmitDetails.setVisibility(View.VISIBLE);

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
                    bitmap= ThumbnailUtils.extractThumbnail(bitmap,512,512);
                    mProfilePic=bitmap;
                    mProfilePicHolder.setImageBitmap(mProfilePic);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
