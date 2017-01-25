package com.rajora.arun.chat.chit.chitchat.fragments;


import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_chat_item;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractChatList;
import com.rajora.arun.chat.chit.chitchat.dataModels.ChatListDataModel;

import java.util.ArrayList;
import java.util.List;

public class fragment_chat_list extends Fragment
		implements LoaderManager.LoaderCallbacks<Cursor>,adapter_chat_item.onItemClickListener{

    private adapter_chat_item mAdapter;
    private static final int CURSOR_LOADER_ID=2;

	private TextView mEmptyView;

	public fragment_chat_list() {
    }

    public static fragment_chat_list newInstance() {
        return new fragment_chat_list();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chat_list, container, false);
	    mEmptyView= (TextView) view.findViewById(R.id.chat_list_empty);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        mAdapter = new adapter_chat_item(getContext(), FirebaseStorage.getInstance(),this,null,
		        ContractChatList._ID);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onItemClick(int position,ChatListDataModel item,CardView img) {
        Intent intent=new Intent(getContext(), ChatActivity.class);
        intent.putExtra("type","contact_data_model");
        intent.putExtra("data",item.getContactItemDataModel());
	    startActivity(intent);
    }

    @Override
    public void onImageClick(int position, ChatListDataModel item, CardView img) {
        Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
        intent.putExtra("type","contact_data_model");
        intent.putExtra("data",item.getContactItemDataModel());
	    ActivityOptionsCompat options = ActivityOptionsCompat.
			    makeSceneTransitionAnimation(getActivity(), (View)img,getString(R.string.pic_transition_name));
	    startActivity(intent,options.toBundle());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), ChatContentProvider.CHATS_LIST_URI,
                new String[]{ContractChatList._ID,
		                ContractChatList.COLUMN_CONTACT_ID,
		                ContractChatList.COLUMN_IS_BOT,
		                ContractChatList.COLUMN_NAME,
		                ContractChatList.COLUMN_LAST_MESSAGE,
		                ContractChatList.COLUMN_LAST_MESSAGE_TIMESTAMP,
		                ContractChatList.COLUMN_LAST_MESSAGE_TYPE,
		                ContractChatList.COLUMN_PIC_URI,
		                ContractChatList.COLUMN_PIC_URL,
		                ContractChatList.COLUMN_UNREAD_COUNT},null,null,
                ContractChatList.COLUMN_LAST_MESSAGE_TIMESTAMP+" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
	    if(data!=null && data.getCount()>0){
		    mEmptyView.setVisibility(View.GONE);
	    }
	    else{
		    mEmptyView.setVisibility(View.VISIBLE);
	    }
	    if (VERSION.SDK_INT >= VERSION_CODES.N_MR1 && data!=null) {
		    data.moveToFirst();
		    List<ShortcutInfo> shortcuts=new ArrayList<>();
		    for(int i=1;i<5 && !data.isAfterLast();){
			    ChatListDataModel item=new ChatListDataModel(data);
			    if(item.name!=null && !item.name.isEmpty()){
				    Intent intent=new Intent(getContext(), ChatActivity.class);
				    intent.putExtra("type","contact_data_model");
				    intent.putExtra("data",item.getContactItemDataModel());
				    ShortcutInfo shortcutInfo=
						    new ShortcutInfo.Builder(getContext(),"id"+ i)
								    .setShortLabel(item.name)
								    .setLongLabel((item.is_bot?"Bot: ":"Contact: ")+item.name)
								    .setIntent(intent)
								    .setIcon(Icon.createWithResource(getContext(),R.drawable.empty_profile_pic))
								    .build();
				    shortcuts.add(shortcutInfo);
				    i++;
			    }
			    data.moveToNext();
		    }
		    ShortcutManager shortcutManager=getContext().getSystemService(ShortcutManager.class);
		    shortcutManager.setDynamicShortcuts(shortcuts);

	    }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
