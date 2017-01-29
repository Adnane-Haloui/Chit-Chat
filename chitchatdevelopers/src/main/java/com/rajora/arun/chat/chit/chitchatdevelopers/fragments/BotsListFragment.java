package com.rajora.arun.chat.chit.chitchatdevelopers.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.activities.BotDetailsActivity;
import com.rajora.arun.chat.chit.chitchatdevelopers.activities.BotEditActivity;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotContentProvider;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotProviderHelper;
import com.rajora.arun.chat.chit.chitchatdevelopers.dataModel.LocalBotDataModel;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;
import com.rajora.arun.chat.chit.chitchatdevelopers.recyclerview_adapters.AdapterBotItem;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class BotsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterBotItem.onItemClickListener {

	private static final int CURSOR_LOADER_ID = 1;
	FloatingActionButton fab;
	DatabaseReference databaseReference;
	ChildEventListener mChildEventListener = new ChildEventListener() {
		@Override
		public void onChildAdded(DataSnapshot dataSnapshot, String s) {
			if (!dataSnapshot.child("is_deleted").getValue(Boolean.class)) {
				BotProviderHelper.AddBot(BotsListFragment.this.getContext(), dataSnapshot.getValue(LocalBotDataModel.class));
			}
		}

		@Override
		public void onChildChanged(DataSnapshot dataSnapshot, String s) {
			if (!dataSnapshot.child("is_deleted").getValue(Boolean.class)) {
				BotProviderHelper.UpdateBot(BotsListFragment.this.getContext(), dataSnapshot.getValue(LocalBotDataModel.class));
			}

		}

		@Override
		public void onChildRemoved(DataSnapshot dataSnapshot) {
		}

		@Override
		public void onChildMoved(DataSnapshot dataSnapshot, String s) {
		}

		@Override
		public void onCancelled(DatabaseError databaseError) {
		}
	};
	private AdapterBotItem mCursorAdapter;
	private String ph_no;

	public BotsListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sharedPreferences = getContext().getSharedPreferences("user-details", MODE_PRIVATE);
		ph_no = sharedPreferences.getString("phone", "null");
		databaseReference = FirebaseDatabase.getInstance().getReference("botItems/" + ph_no.substring(1) + "/");

	}

	@Override
	public void onStart() {
		super.onStart();
		databaseReference.addChildEventListener(mChildEventListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (databaseReference != null) {
			databaseReference.removeEventListener(mChildEventListener);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_bots_list, container, false);
		fab = (FloatingActionButton) v.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BotEditActivity.class);
				startActivity(intent);
			}
		});
		RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
		mCursorAdapter = new AdapterBotItem(getContext(), this, null, BotContracts._ID);
		mRecyclerView.setAdapter(mCursorAdapter);
		return v;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getContext(), BotContentProvider.CONTENT_URI,
				new String[]{BotContracts._ID,
						BotContracts.COLUMN_ID,
						BotContracts.COLUMN_GLOBAL_ID,
						BotContracts.COLUMN_BOT_NAME,
						BotContracts.COLUMN_ABOUT,
						BotContracts.COLUMN_PIC_URI}, null, null, BotContracts.COLUMN_BOT_NAME);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCursorAdapter.swapCursor(null);
	}

	@Override
	public void onItemClick(int position, LocalBotDataModel item) {
		Intent intent = new Intent(getContext(), BotDetailsActivity.class);
		intent.putExtra("data", item.Gid);
		intent.putExtra("id", item.id);
		startActivity(intent);
	}

	@Override
	public void onEditClick(int position, LocalBotDataModel item) {
		Intent intent = new Intent(getContext(), BotEditActivity.class);
		intent.putExtra("data", item.Gid);
		intent.putExtra("id", item.id);
		startActivity(intent);
	}

	@Override
	public void onDeleteClick(int position, LocalBotDataModel item) {
		getContext().getContentResolver().delete(BotContentProvider.CONTENT_URI, BotContracts.COLUMN_GLOBAL_ID + " = ?", new String[]{item.Gid});
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("is_deleted", true);
		FirebaseDatabase.getInstance().getReference().child("botItems/" + ph_no.substring(1) + "/" + item.id).updateChildren(values);
		FirebaseDatabase.getInstance().getReference().child("botList/" + item.Gid).updateChildren(values);
	}
}
