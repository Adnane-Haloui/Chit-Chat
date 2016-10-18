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
 * Created by arc on 15/10/16.
 */

public class adapter_contact_item  extends RecyclerView.Adapter<adapter_contact_item.VH>{


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.view_contact_item,parent,false);
        return new VH(v);
    }

    public onItemClickListener mItemClickListener;
    public adapter_contact_item(onItemClickListener listener)
    {
        mItemClickListener=listener;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bind(mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return 2;
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
        public void bind(final onItemClickListener mItemClickListener) {
            itemView.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onItemClick(getAdapterPosition());
                        }
                    }
            );
            itemView.findViewById(R.id.contact_item_image_container).setOnClickListener(new View.OnClickListener() {
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
