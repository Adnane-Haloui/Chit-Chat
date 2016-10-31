package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chat;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

/**
 * Created by arc on 18/10/16.
 */

public class adapter_chats extends CursorRecyclerViewAdapter<adapter_chats.VH>{

    public String mSender_No;
    public adapter_chats(String sender_no,Cursor cursor, String idColumn) {
        super(cursor, idColumn);
        mSender_No=sender_no;
    }
    @Override
    public void onBindViewHolder(VH holder, Cursor cursor) {
        if(cursor.getString(cursor.getColumnIndex(contract_chat.COLUMN_MESSAGE_TYPE)).equals("text")){
            holder.itemView.setContentDescription("message "+cursor.getColumnIndex(contract_chat.COLUMN_MESSAGE)+
                    " . Time "+ utils.getTimeFromTimestamp(
                            cursor.getString(cursor.getColumnIndex(contract_chat.COLUMN_TIMESTAMP)),false));
            holder.mMessage.setText(cursor.getString(cursor.getColumnIndex(contract_chat.COLUMN_MESSAGE)));
            holder.mTime.setText(utils.getTimeFromTimestamp(
                    cursor.getString(cursor.getColumnIndex(contract_chat.COLUMN_TIMESTAMP)),false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mCursor!=null && mCursor.moveToPosition(position)){
            return mCursor.getString(mCursor.getColumnIndex(contract_chat.COLUMN_MESSAGE_SENDER_NUMBER)).equals(mSender_No)?1:2;
        }
        return 0;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType==1){
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_send,parent,false);
        }
        else if(viewType==2){
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chat_receive,parent,false);
        }
        else{
            v=null;
        }
        return new adapter_chats.VH(v);
    }

    public static class VH extends RecyclerView.ViewHolder{

        TextView mMessage;
        TextView mTime;

        public VH(View itemView) {
            super(itemView);
            mMessage = ((TextView) itemView.findViewById(R.id.text_message));
            mTime = ((TextView) itemView.findViewById(R.id.text_time));

        }
    }
}
