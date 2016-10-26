package com.rajora.arun.chat.chit.chitchat.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.bot_VH;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_bot_item;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ProviderHelper;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_bots;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;
import com.rajora.arun.chat.chit.chitchat.dataModels.BotsDataModel;

public class fragment_bot_list extends Fragment{
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int position;
    private static final int CURSOR_LOADER_ID=1;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    public fragment_bot_list() {
    }

    public static fragment_bot_list newInstance(int position) {
        fragment_bot_list fragment = new fragment_bot_list();
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_bot_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.bot_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseStorage=FirebaseStorage.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("botList");
        mAdapter = new FirebaseRecyclerAdapter<BotsDataModel,bot_VH>(
            BotsDataModel.class,R.layout.view_bot_item,bot_VH.class,databaseReference) {
            @Override
            protected void populateViewHolder(bot_VH viewHolder, final BotsDataModel model, int position) {
                CardView mImageContainerCardView = ((CardView) viewHolder.itemView.findViewById(R.id.bot_item_image_container));
                ImageView mImage = ((ImageView) viewHolder.itemView.findViewById(R.id.bot_item_image));
                TextView mName = ((TextView) viewHolder.itemView.findViewById(R.id.bot_item_name));
                TextView mDeveloperName = ((TextView) viewHolder.itemView.findViewById(R.id.bot_item_developer_name));
                TextView mAbout = ((TextView) viewHolder.itemView.findViewById(R.id.bot_item_about));

                mName.setText(model.getName());
                mDeveloperName.setText(model.getDev_name());
                mAbout.setText(model.getDesc());
                if(model.getImage_url()!=null){
                    Glide.with(fragment_bot_list.this.getContext())
                            .using(new FirebaseImageLoader())
                            .load(firebaseStorage.getReference(model.getImage_url()))
                            .into(mImage);
                }
                mImageContainerCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
                        intent.putExtra("type","bot");
                        intent.putExtra("imageurl",model.getImage_url());
                        intent.putExtra("text1",model.getName());
                        intent.putExtra("text2",model.getDev_name());
                        intent.putExtra("text3",model.getDesc());
                        startActivity(intent);

                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("type","bot");
                        intent.putExtra("imageurl",model.getImage_url());
                        intent.putExtra("name",model.getName());
                        intent.putExtra("dev_name",model.getDev_name());
                        intent.putExtra("about",model.getDesc());
                        intent.putExtra("Gid",model.getGid());
                        startActivity(intent);

                    }
                });

            }
        };
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}
