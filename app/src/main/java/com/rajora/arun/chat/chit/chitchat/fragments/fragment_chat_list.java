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

import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_chat_item;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chats;

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
        mAdapter = new adapter_chat_item(this,null,contract_chats.COLUMN_ID);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onItemClick(int position,Cursor cursor) {
        Intent intent=new Intent(getContext(), ChatActivity.class);

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
                        contract_chats.COLUMN_ABOUT},null,null,null);
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
