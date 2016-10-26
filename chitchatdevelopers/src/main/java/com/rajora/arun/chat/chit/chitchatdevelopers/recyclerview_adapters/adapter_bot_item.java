package com.rajora.arun.chat.chit.chitchatdevelopers.recyclerview_adapters;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;

/**
 * Created by arc on 19/10/16.
 */

public class adapter_bot_item extends CursorRecyclerViewAdapter<adapter_bot_item.VH>{

    public onItemClickListener mItemClickListener;
    public adapter_bot_item(onItemClickListener listener,Cursor cursor,String idColumn)
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
        byte[] img=cursor.getBlob(cursor.getColumnIndex(BotContracts.COLUMN_PIC));
        if(img!=null && img.length>20)
            holder.mImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
        holder.mName.setText(cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_BOT_NAME)));
        holder.mAbout.setText(cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_ABOUT)));

    }

    public static class VH extends RecyclerView.ViewHolder{
        public CardView mImageContainerCardView;
        public ImageView mImage;
        public TextView mName;
        public TextView mAbout;

        public VH(View itemView) {
            super(itemView);
            mImageContainerCardView = ((CardView) itemView.findViewById(R.id.bot_item_image_container));
            mImage = ((ImageView) itemView.findViewById(R.id.bot_item_image));
            mName = ((TextView) itemView.findViewById(R.id.bot_item_name));
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

