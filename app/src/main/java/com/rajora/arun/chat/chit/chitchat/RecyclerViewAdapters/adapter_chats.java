package com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by arc on 18/10/16.
 */

public class adapter_chats extends CursorRecyclerViewAdapter<adapter_chats.VH>{

    public adapter_chats(Cursor cursor, String idColumn) {
        super(cursor, idColumn);
    }
    @Override
    public void onBindViewHolder(VH holder, Cursor cursor) {

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public static class VH extends RecyclerView.ViewHolder{

        public VH(View itemView) {
            super(itemView);
        }
    }
}
