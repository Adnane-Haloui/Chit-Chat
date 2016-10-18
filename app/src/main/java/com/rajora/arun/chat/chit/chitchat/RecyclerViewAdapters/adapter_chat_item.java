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

public class adapter_chat_item extends RecyclerView.Adapter<adapter_chat_item.VH>{

    public onItemClickListener mItemClickListener;
    public adapter_chat_item(onItemClickListener listener)
    {
        mItemClickListener=listener;
    }

    @Override
    public adapter_chat_item.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_item,parent,false);
        return new adapter_chat_item.VH(v);
    }

    @Override
    public void onBindViewHolder(adapter_chat_item.VH holder, int position) {
        holder.bind(mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return 15;
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
        public void bind(final onItemClickListener mItemClickListener) {
            itemView.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onItemClick(getAdapterPosition());
                        }
                    }
            );
            itemView.findViewById(R.id.chat_item_image_container).setOnClickListener(new View.OnClickListener() {
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
