package com.rajora.arun.chat.chit.chitchat.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_chats;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_chat;
import com.rajora.arun.chat.chit.chitchat.services.SendMessageService;
import com.rajora.arun.chat.chit.chitchat.utils.utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public ChatActivityFragment() {
    }

    private String type;
    private String to_id;
    private boolean is_bot;

    private Bundle bot_details;

    private static final int CURSOR_LOADER_ID=200;

    private RecyclerView mRecyclerView;
    private adapter_chats mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type", "");
            if (type.equals("contact")) {
                is_bot = false;
                to_id = extras.getString("number", "");
            }
            else if (type.equals("bot")) {
                is_bot = true;
                to_id = extras.getString("Gid", "");

                String bot_name=extras.getString("name","");
                String bot_number=extras.getString("dev_no","");
                String bot_about=extras.getString("about","");
                String bot_gid=extras.getString("Gid","");
                String bot_image_url=extras.getString("imageurl","");
                String bot_dev_name=extras.getString("dev_name","");
                bot_details=new Bundle();
                bot_details.putString("botName",bot_name);
                bot_details.putString("botNumber",bot_number);
                bot_details.putString("botAbout",bot_about);
                bot_details.putString("botGid",bot_gid);
                bot_details.putString("botImageUrl",bot_image_url);
                bot_details.putString("botDevName",bot_dev_name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.chat_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("user-details",MODE_PRIVATE);
        final String ph_no=sharedPreferences.getString("phone","");
        mRecyclerView.setLayoutManager(mLayoutManager);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        mAdapter = new adapter_chats("+"+ph_no,null, contract_chat.COLUMN_ID);
        mRecyclerView.setAdapter(mAdapter);

        view.findViewById(R.id.send_message_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_bot){
                    SendMessageService.startSendTextMessageToBot(getContext(),"+"+ph_no,to_id,"text",
                            ((EditText) view.findViewById(R.id.message)).getText().toString(), utils.getCurrentTimestamp(),bot_details);
                }
                else{
                    SendMessageService.startSendTextMessageToUser(getContext(),"+"+ph_no,to_id,"text",
                            ((EditText) view.findViewById(R.id.message)).getText().toString(), utils.getCurrentTimestamp());
                }
                ((EditText) view.findViewById(R.id.message)).setText("");
            }
        });

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), ChatContentProvider.CHAT_URI,
                new String[]{contract_chat.COLUMN_ID,
                        contract_chat.COLUMN_MESSAGE_STATUS,
                        contract_chat.COLUMN_MESSAGE,
                        contract_chat.COLUMN_MESSAGE_TYPE,
                        contract_chat.COLUMN_IS_BOT,
                        contract_chat.COLUMN_MESSAGE_ID_ON_SERVER,
                        contract_chat.COLUMN_MESSAGE_SENDER_ID,
                        contract_chat.COLUMN_MESSAGE_SENDER_NUMBER,
                        contract_chat.COLUMN_TIMESTAMP},contract_chat.
                COLUMN_MESSAGE_SENDER_ID+" = ?" ,new String[]{to_id},contract_chat.COLUMN_TIMESTAMP+" ASC");
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
