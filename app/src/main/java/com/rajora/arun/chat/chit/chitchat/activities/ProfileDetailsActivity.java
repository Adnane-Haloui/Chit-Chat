package com.rajora.arun.chat.chit.chitchat.activities;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.fragments.fragment_bot_list;

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
}
