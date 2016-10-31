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

/**
 * Created by arc on 17/10/16.
 */

public class adapter_bot_item extends CursorRecyclerViewAdapter<adapter_bot_item.VH>{

    private onItemClickListener mItemClickListener;
    public adapter_bot_item(onItemClickListener listener, Cursor cursor, String idColumn)
    {
        super(cursor,idColumn);
        mItemClickListener=listener;
    }

    @Override
    public adapter_bot_item.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bot_item,parent,false);
        return new adapter_bot_item.VH(v);
    }

    @Override
    public void onBindViewHolder(adapter_bot_item.VH holder, Cursor cursor) {
        byte[] img=cursor.getBlob(cursor.getColumnIndex(contract_bots.COLUMN_PIC));
        if(img!=null && img.length>20)
            holder.mImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
        holder.mName.setText(cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_NAME)));
        holder.mAbout.setText(cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_ABOUT)));
        holder.mDeveloperName.setText(cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_DEVELOPER_NAME)));

        holder.itemView.setContentDescription("Bot item "+cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_NAME))+" , developed by "+
                cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_DEVELOPER_NAME)));
        holder.mDeveloperName.setContentDescription("Bot developed by "+
                cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_DEVELOPER_NAME)));
        holder.mName.setContentDescription("Bot name is "+cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_NAME)));
        holder.mAbout.setContentDescription(cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_ABOUT)));
        holder.mImage.setContentDescription("Profile picture of "+
                cursor.getString(cursor.getColumnIndex(contract_bots.COLUMN_NAME)));

        holder.bind(mItemClickListener);
    }

    public static class VH extends RecyclerView.ViewHolder{
        CardView mImageContainerCardView;
        ImageView mImage;
        TextView mName;
        TextView mDeveloperName;
        TextView mAbout;

        public VH(View itemView) {
            super(itemView);
            mImageContainerCardView = ((CardView) itemView.findViewById(R.id.bot_item_image_container));
            mImage = ((ImageView) itemView.findViewById(R.id.bot_item_image));
            mName = ((TextView) itemView.findViewById(R.id.bot_item_name));
            mDeveloperName = ((TextView) itemView.findViewById(R.id.bot_item_developer_name));
            mAbout = ((TextView) itemView.findViewById(R.id.bot_item_about));

        }
        public void bind(final onItemClickListener mItemClickListener) {
            itemView.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onItemClick(getAdapterPosition());
                        }
                    }
            );
            itemView.findViewById(R.id.bot_item_image_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onImageClick(getAdapterPosition());
                }
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(int position);
        void onImageClick(int position);
    }
}
