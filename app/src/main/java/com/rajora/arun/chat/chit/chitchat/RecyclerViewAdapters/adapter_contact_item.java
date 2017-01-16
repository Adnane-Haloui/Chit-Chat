package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactDetailDataModel;
import com.rajora.arun.chat.chit.chitchat.utils.ImageUtils;

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
        ContactDetailDataModel item=new ContactDetailDataModel(cursor);
        if(item.is_user){
            holder.mIsUserTextView.setText("User");
        }
        else{
            holder.mIsUserTextView.setText("Invite");
        }
        holder.mNumber.setText(item.contact_id);
        holder.mName.setText(item.name==null || item.name.isEmpty()?item.contact_id:item.name);
        holder.mAbout.setText(item.about==null?"":item.about);
        ImageUtils.loadImageIntoView(mContext,item,holder.mImage);

        holder.itemView.setContentDescription("Contact item with name "+
                (item.name==null || item.name.isEmpty()?"Unknown":item.name)+" and number "+item.contact_id);
        holder.mNumber.setContentDescription("Contact phone number "+item.contact_id);
        holder.mName.setContentDescription("Contact name "+(item.name==null || item.name.isEmpty()?item.contact_id:item.name));
        holder.mAbout.setContentDescription("About "+
                (item.name==null || item.name.isEmpty()?item.contact_id:item.name)+" : "+(item.about==null?"":item.about));
        holder.mImage.setContentDescription("Profile picture of "+item.name);

        bind(holder,mItemClickListener,item);
    }

    public void bind(final VH holder,final onItemClickListener mItemClickListener,final ContactDetailDataModel item) {
        holder.itemView.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(holder.getAdapterPosition(),item);
                    }
                }
        );
        holder.itemView.findViewById(R.id.contact_item_image_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onImageClick(holder.getAdapterPosition(),item);
            }
        });
    }
    public static class VH extends RecyclerView.ViewHolder{

        TextView mIsUserTextView;
        CardView mImageContainerCardView;
        ImageView mImage;
        TextView mName;
        TextView mAbout;
        TextView mNumber;

        public VH(View itemView) {
            super(itemView);
            mImageContainerCardView = ((CardView) itemView.findViewById(R.id.contact_item_image_container));
            mImage = (ImageView) itemView.findViewById(R.id.contact_item_image);
            mName = (TextView) itemView.findViewById(R.id.contact_item_name);
            mAbout = (TextView) itemView.findViewById(R.id.contact_item_about);
            mNumber = (TextView) itemView.findViewById(R.id.contact_item_number);
            mIsUserTextView = (TextView) itemView.findViewById(R.id.contact_is_user_textview);
        }
    }

    public interface onItemClickListener{
        void onItemClick(int position,ContactDetailDataModel item);
        void onImageClick(int position,ContactDetailDataModel item);
    }
}
