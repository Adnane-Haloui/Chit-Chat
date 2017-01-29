package com.rajora.arun.chat.chit.chitchat.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.Bot_VH;
import com.rajora.arun.chat.chit.chitchat.dataModels.FirebaseBotsDataModel;

public class FragmentBotList extends Fragment {

	private RecyclerView mRecyclerView;
	private FirebaseRecyclerAdapter mAdapter;
	private DatabaseReference databaseReference;

	private TextView mEmptyView;

	public FragmentBotList() {
	}

	public static FragmentBotList newInstance() {
		return new FragmentBotList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_bot_list, container, false);
		mEmptyView = (TextView) view.findViewById(id.bot_list_empty);
		mRecyclerView = (RecyclerView) view.findViewById(id.bot_recycler_view);
		LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);
		databaseReference = FirebaseDatabase.getInstance().getReference("botList");
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mAdapter = new FirebaseRecyclerAdapter<FirebaseBotsDataModel, Bot_VH>(
				FirebaseBotsDataModel.class, R.layout.view_bot_item, Bot_VH.class, databaseReference) {
			@Override
			protected void populateViewHolder(Bot_VH viewHolder, final FirebaseBotsDataModel model, int position) {
				viewHolder.setValues(model, getContext());
				viewHolder.setContentDescription(model, getContext());
				viewHolder.setClickListeners(model, FragmentBotList.this);
			}

			@Override
			protected void onDataChanged() {
				mEmptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
			}

		};
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAdapter != null) {
			mAdapter.cleanup();
		}
	}

}
