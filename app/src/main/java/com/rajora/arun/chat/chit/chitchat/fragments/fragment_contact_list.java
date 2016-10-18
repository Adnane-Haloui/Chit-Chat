package com.rajora.arun.chat.chit.chitchat.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_contact_item;

public class fragment_contact_list extends Fragment implements adapter_contact_item.onItemClickListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int position;


    public fragment_contact_list() {
    }
    public static fragment_contact_list newInstance(int position) {
        fragment_contact_list fragment = new fragment_contact_list();
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

        View view=inflater.inflate(R.layout.fragment_contact_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new adapter_contact_item(this);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
    @Override
    public void onItemClick(int position) {
        Intent intent=new Intent(getContext(), ChatActivity.class);

        startActivity(intent);
    }

    @Override
    public void onImageClick(int position) {
        Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);

        startActivity(intent);

    }
}
