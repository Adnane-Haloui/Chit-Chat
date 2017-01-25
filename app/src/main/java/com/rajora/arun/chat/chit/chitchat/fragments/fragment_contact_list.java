package com.rajora.arun.chat.chit.chitchat.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_contact_item;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.ContractContacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.ContactDetailDataModel;
import com.rajora.arun.chat.chit.chitchat.services.updateContactsDbFromPhoneDb;

import static android.app.Activity.RESULT_OK;

public class fragment_contact_list extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,adapter_contact_item.onItemClickListener{

	private static final int TAG_ADD_CONTACT=105;

	private adapter_contact_item mAdapter;

	private SwipeRefreshLayout refreshLayout;
	private TextView mEmptyView;

	private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshLayout.setRefreshing(false);
		}
	};
	private final IntentFilter mStatusIntentFilter = new IntentFilter(updateContactsDbFromPhoneDb.BROADCAST_ACTION);

    private static final int CURSOR_LOADER_ID=3;

    public fragment_contact_list() {
    }
    public static fragment_contact_list newInstance() {
        return new fragment_contact_list();
    }

	@Override
	public void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,mStatusIntentFilter);
		startContactRefresh();
	}

	@Override
	public void onStop() {
		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contact_list, container, false);
		mEmptyView= (TextView) view.findViewById(R.id.contact_list_empty);
		FloatingActionButton refreshFab = (FloatingActionButton) view.findViewById(R.id.refresh_fab);
		FloatingActionButton addContactFab = (FloatingActionButton) view.findViewById(R.id.add_contact_fab);
	    refreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
		refreshLayout.setRefreshing(updateContactsDbFromPhoneDb.isProcessing);
		addContactFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent add_contact_intent = new Intent(ContactsContract.Intents.Insert.ACTION);
				add_contact_intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
				startActivityForResult(add_contact_intent,TAG_ADD_CONTACT);

			}
		});
		refreshFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startContactRefresh();
			}
		});
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				startContactRefresh();
			}
		});
		RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        mAdapter=new adapter_contact_item(getContext(),this,null, ContractContacts._ID);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==200 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
	        if(!updateContactsDbFromPhoneDb.isProcessing){
		        updateContactsDbFromPhoneDb.startContactDbUpdate(getContext());
	        }
        }
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK && requestCode==TAG_ADD_CONTACT){
			startContactRefresh();
		}
	}

	@Override
    public void onItemClick(int position,ContactDetailDataModel item,CardView img) {
        Intent intent=new Intent(getContext(), ChatActivity.class);
        intent.putExtra("type","contact_data_model");
        intent.putExtra("data",item.getContactItemDataModel());
		startActivity(intent);
    }

    @Override
    public void onImageClick(int position, ContactDetailDataModel item, CardView img) {
        Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
        intent.putExtra("type","contact_data_model");
        intent.putExtra("data",item.getContactItemDataModel());
	    ActivityOptionsCompat options = ActivityOptionsCompat.
			    makeSceneTransitionAnimation(getActivity(), (View)img,getString(R.string.pic_transition_name));
	    startActivity(intent,options.toBundle());

    }

	public void startContactRefresh(){
		if(!updateContactsDbFromPhoneDb.isProcessing){
			if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},200);
			}
			else{
				refreshLayout.setRefreshing(true);
				updateContactsDbFromPhoneDb.startContactDbUpdate(getContext());
			}
		}
	}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getContext(),ChatContentProvider.CONTACT_LIST_URI,
                new String[]{
                        ContractContacts._ID,
                        ContractContacts.COLUMN_NAME,
                        ContractContacts.COLUMN_ABOUT,
                        ContractContacts.COLUMN_CONTACT_ID,
                        ContractContacts.COLUMN_IS_USER,
                        ContractContacts.TN_COLUMN_PIC_URI,
                        ContractContacts.COLUMN_PIC_URL},
                ContractContacts.COLUMN_IS_BOT+" = ? ",new String[]{"0"},
                ContractContacts.COLUMN_IS_USER+" DESC , "+ContractContacts.COLUMN_NAME+" COLLATE NOCASE ");
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
