package com.rajora.arun.chat.chit.chitchat.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_chat_item;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chats;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;

public class fragment_chat_list extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,adapter_chat_item.onItemClickListener{
    private RecyclerView mRecyclerView;
    private adapter_chat_item mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int position;
    private static final int CURSOR_LOADER_ID=2;

    public fragment_chat_list() {
    }

    public static fragment_chat_list newInstance(int position) {
        fragment_chat_list fragment = new fragment_chat_list();
        Bundle bundle=new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chat_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        mAdapter = new adapter_chat_item(getContext(), FirebaseStorage.getInstance(),this,null,contract_chats.COLUMN_ID);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onItemClick(int position,Cursor cursor) {
        Intent intent=new Intent(getContext(), ChatActivity.class);
        if(cursor.getInt(cursor.getColumnIndex(contract_chats.COLUMN_IS_BOT))>0){
            intent.putExtra("type","bot");
            intent.putExtra("imageurl",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_BOT_PIC_URL)));
            intent.putExtra("name",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_NAME)));
            intent.putExtra("dev_name",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_BOT_DEV_NAME)));
            intent.putExtra("about",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_ABOUT)));
            intent.putExtra("Gid",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_BOT_ID)));

        }
        else{
            intent.putExtra("type","contact");
            intent.putExtra("profilePic",cursor.getBlob(cursor.getColumnIndex(contract_chats.COLUMN_PIC)));
            intent.putExtra("name",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_NAME)));
            intent.putExtra("number",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_PH_NUMBER)));
            intent.putExtra("about",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_ABOUT)));
        }

        startActivity(intent);
    }

    @Override
    public void onImageClick(int position,Cursor cursor) {
        Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
        intent.putExtra("type","chats");
        intent.putExtra("image",cursor.getBlob(cursor.getColumnIndex(contract_chats.COLUMN_PIC)));
        intent.putExtra("text1",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_NAME)));
        intent.putExtra("text2",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_PH_NUMBER)));
        intent.putExtra("text3",cursor.getString(cursor.getColumnIndex(contract_chats.COLUMN_ABOUT)));

        startActivity(intent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), ChatContentProvider.CHATS_URI,
                new String[]{contract_chats.COLUMN_LAST_MESSAGE_TIME,
                        contract_chats.COLUMN_LAST_MESSAGE,
                        contract_chats.COLUMN_BOT_ID,
                        contract_chats.COLUMN_PH_NUMBER,
                        contract_chats.COLUMN_NAME,
                        contract_chats.COLUMN_IS_BOT,
                        contract_chats.COLUMN_PIC,
                        contract_chats.COLUMN_ID,
                        contract_chats.COLUMN_ABOUT,
                        contract_chats.COLUMN_BOT_PIC_URL,
                        contract_chats.COLUMN_BOT_DEV_NAME},null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
