package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_bots;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chats;

/**
 * Created by arc on 17/10/16.
 */

public class adapter_chat_item extends CursorRecyclerViewAdapter<adapter_chat_item.VH>{

    public onItemClickListener mItemClickListener;
    public adapter_chat_item(onItemClickListener listener, Cursor cursor, String idColumn)
    {
        super(cursor,idColumn);
        mItemClickListener=listener;
    }

    @Override
    public adapter_chat_item.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_item,parent,false);
        return new adapter_chat_item.VH(v);
    }


    @Override
    public void onBindViewHolder(adapter_chat_item.VH holder, Cursor cursor) {
        byte[] img=cursor.getBlob(cursor.getColumnIndex(contract_chats.COLUMN_PIC));
        if(img!=null && img.length>20)
            holder.mImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));

        holder.mName.setText(cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_NAME)));
        holder.mAbout.setText(cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_LAST_MESSAGE)));
        holder.mTime.setText(cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_LAST_MESSAGE_TIME)));
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
        holder.itemView.findViewById(R.id.chat_item_image_container).setOnClickListener(new View.OnClickListener() {
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
        TextView mTime;

        public VH(View itemView) {
            super(itemView);
            mImageContainerCardView = ((CardView) itemView.findViewById(R.id.chat_item_image_container));
            mImage = ((ImageView) itemView.findViewById(R.id.chat_item_image));
            mName = ((TextView) itemView.findViewById(R.id.chat_item_name));
            mAbout = ((TextView) itemView.findViewById(R.id.chat_item_about));
            mTime = ((TextView) itemView.findViewById(R.id.chat_item_time));

        }
    }
    public interface onItemClickListener{
         void onItemClick(int position,Cursor cursor);
         void onImageClick(int position,Cursor cursor);
    }
}
