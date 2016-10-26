package com.rajora.arun.chat.chit.chitchatdevelopers.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.activities.BotEditActivity;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotContentProvider;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotProviderHelper;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;
import com.rajora.arun.chat.chit.chitchatdevelopers.recyclerview_adapters.adapter_bot_item;

import static android.content.Context.MODE_PRIVATE;

public class BotsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,adapter_bot_item.onItemClickListener{

    FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private adapter_bot_item mCursorAdapter;
    private static final int CURSOR_LOADER_ID=1;
    public BotsListFragment() {
    }
 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     SharedPreferences sharedPreferences=getContext().getSharedPreferences("user-details",MODE_PRIVATE);
     final String ph_no=sharedPreferences.getString("phone","null");
     DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("botItems/"+ph_no+"/");
     databaseReference.addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(DataSnapshot dataSnapshot, String s) {
             StorageReference reference=FirebaseStorage.getInstance().getReference(ph_no+"/botitem/"+dataSnapshot.getKey().toString()+"/");
           /* BotProviderHelper.AddBot(BotsListFragment.this.getContext(),dataSnapshot.getKey().toString(),
                    dataSnapshot.child("name").getValue().toString(),
                    dataSnapshot.child("about").getValue().toString(),
                    dataSnapshot.child("url").getValue().toString(),
                    dataSnapshot.child("secret").getValue().toString(),
                    dataSnapshot.child("pic").getValue().toString().getBytes());
         */
         }

         @Override
         public void onChildChanged(DataSnapshot dataSnapshot, String s) {
             return;
         }

         @Override
         public void onChildRemoved(DataSnapshot dataSnapshot) {
             return;
         }

         @Override
         public void onChildMoved(DataSnapshot dataSnapshot, String s) {
             return;
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_bots_list, container, false);
        fab=(FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), BotEditActivity.class);
                startActivity(intent);
            }
        });
        mRecyclerView=(RecyclerView) v.findViewById(R.id.recycler_view);
        mLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        mCursorAdapter = new adapter_bot_item(this, null,BotContracts.COLUMN_ID);
        mRecyclerView.setAdapter(mCursorAdapter);
        return v;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), BotContentProvider.CONTENT_URI,
                new String[]{  BotContracts.COLUMN_ID, BotContracts.COLUMN_BOT_NAME,
                        BotContracts.COLUMN_ABOUT,BotContracts.COLUMN_PIC},null,null,null);
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
    public void onItemClick(int position) {

    }

    @Override
    public void onImageClick(int position) {

    }
}
