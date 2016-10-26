package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ProviderHelper;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

/**
 * Created by arc on 15/10/16.
 */

public class adapter_contact_item  extends CursorRecyclerViewAdapter<adapter_contact_item.VH>{

    private onItemClickListener mItemClickListener;
    private Context mContext;

    public adapter_contact_item(Context context,onItemClickListener listener, Cursor cursor, String idColumn)
    {
        super(cursor,idColumn);
        mContext=context;
        mItemClickListener=listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact_item,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(final VH holder, Cursor cursor) {
        String name=cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_NAME));
        String ph_no=cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_PH_NUMBER));
        String about=cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_ABOUT));
        byte[] img=cursor.getBlob(cursor.getColumnIndex(contract_contacts.COLUMN_PIC));

        holder.mNumber.setText(ph_no);
        holder.mName.setText(name==null?"":name);
        holder.mAbout.setText(about==null?"":about);
        if(img!=null && img.length>20)
            holder.mImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.empty_profile_pic,null));
        }
        else{
            holder.mImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.empty_profile_pic));
        }
        bind(holder,mItemClickListener);
    }

    public void bind(final VH holder,final onItemClickListener mItemClickListener) {
        holder.itemView.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(holder.getAdapterPosition());
                        mItemClickListener.onItemClick(holder.getAdapterPosition(),mCursor);
                    }
                }
        );
        holder.itemView.findViewById(R.id.contact_item_image_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToPosition(holder.getAdapterPosition());
                mItemClickListener.onImageClick(holder.getAdapterPosition(),mCursor);
            }
        });
    }
    public static class VH extends RecyclerView.ViewHolder{

        CardView mImageContainerCardView;
        ImageView mImage;
        TextView mName;
        TextView mAbout;
        TextView mNumber;

        public VH(View itemView) {
            super(itemView);
            mImageContainerCardView = ((CardView) itemView.findViewById(R.id.contact_item_image_container));
            mImage = ((ImageView) itemView.findViewById(R.id.contact_item_image));
            mName = ((TextView) itemView.findViewById(R.id.contact_item_name));
            mAbout = ((TextView) itemView.findViewById(R.id.contact_item_about));
            mNumber = ((TextView) itemView.findViewById(R.id.contact_item_number));

        }
    }

    public interface onItemClickListener{
        void onItemClick(int position,Cursor cursor);
        void onImageClick(int position,Cursor cursor);
    }
}
