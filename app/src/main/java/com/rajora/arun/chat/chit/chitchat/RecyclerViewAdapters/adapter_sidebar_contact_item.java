package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

/**
 * Created by arc on 29/10/16.
 */

public class adapter_sidebar_contact_item extends CursorRecyclerViewAdapter<adapter_sidebar_contact_item.VH>{

    private onItemClickListener mItemClickListener;
    private Context mContext;

    public adapter_sidebar_contact_item(Context context, onItemClickListener listener, Cursor cursor, String idColumn)
    {
        super(cursor,idColumn);
        mContext=context;
        mItemClickListener=listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_sidebar_item,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(final VH holder, Cursor cursor) {

        String name=cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_NAME));
        String about=cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_ABOUT));
        byte[] img=cursor.getBlob(cursor.getColumnIndex(contract_contacts.COLUMN_PIC));

        holder.mName.setText(name==null?"":name);
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
        holder.mSendButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(holder.getAdapterPosition());
                        mItemClickListener.onSendClick(holder.getAdapterPosition(),mCursor,holder);
                    }
                }
        );
        holder.mImageContainerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onContactClick(holder.getAdapterPosition(),holder);
            }
        });
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onContactClick(holder.getAdapterPosition(),holder);
            }
        });
    }
    public static class VH extends RecyclerView.ViewHolder{

        public CardView mImageContainerCardView;
        public ImageView mImage;
        public TextView mName;
        public EditText mMessage;
        public CardView mSendButton;
        public LinearLayout sidebarPanel;

        public VH(View itemView) {
            super(itemView);

            mImageContainerCardView = ((CardView) itemView.findViewById(R.id.sidebar_image_container));
            mImage = ((ImageView) itemView.findViewById(R.id.sidebar_image));
            mName = ((TextView) itemView.findViewById(R.id.sidebar_name));
            mMessage = ((EditText) itemView.findViewById(R.id.sidebar_message));
            mSendButton = ((CardView) itemView.findViewById(R.id.sidebar_send_message_button));
            sidebarPanel = ((LinearLayout) itemView.findViewById(R.id.sidebar_panel));
        }
    }

    public interface onItemClickListener{
        void onSendClick(int position,Cursor cursor,VH holder);
        void onContactClick(int position,VH holder);
    }
}
