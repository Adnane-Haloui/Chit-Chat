package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;

/**
 * Created by arc on 17/10/16.
 */

public class adapter_bot_item extends RecyclerView.Adapter<adapter_bot_item.VH>{

    public onItemClickListener mItemClickListener;
    public adapter_bot_item(onItemClickListener listener)
    {
        mItemClickListener=listener;
    }

    @Override
    public adapter_bot_item.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bot_item,parent,false);
        return new adapter_bot_item.VH(v);
    }

    @Override
    public void onBindViewHolder(adapter_bot_item.VH holder, int position) {
        holder.bind(mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return 5;
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
