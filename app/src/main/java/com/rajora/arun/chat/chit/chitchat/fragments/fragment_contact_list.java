package com.rajora.arun.chat.chit.chitchat.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.rajora.arun.chat.chit.chitchat.activities.ChatActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileDetailsActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.RecyclerViewAdapters.adapter_contact_item;
import com.rajora.arun.chat.chit.chitchat.contentProviders.ChatContentProvider;
import com.rajora.arun.chat.chit.chitchat.dataBase.Contracts.contract_contacts;
import com.rajora.arun.chat.chit.chitchat.dataBase.Helper.chat_database;
import com.rajora.arun.chat.chit.chitchat.services.updateContactsDbFromPhoneDb;

public class fragment_contact_list extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,adapter_contact_item.onItemClickListener{

    private RecyclerView mRecyclerView;
    private adapter_contact_item mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int position;
    private static final int CURSOR_LOADER_ID=3;
    private int refreshed=0;

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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},200);
        }
        else if(refreshed==0){
            refreshed=1;
            updateContactsDbFromPhoneDb.startContactDbUpdate(getContext());
        }
        View view=inflater.inflate(R.layout.fragment_contact_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        mAdapter=new adapter_contact_item(getContext(),this,null, contract_contacts.COLUMN_PH_NUMBER);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==200 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(refreshed==0){
                refreshed=1;
                updateContactsDbFromPhoneDb.startContactDbUpdate(getContext());
            }
        }
    }

    @Override
    public void onItemClick(int position,Cursor cursor) {
        Intent intent=new Intent(getContext(), ChatActivity.class);
        intent.putExtra("type","contact");
        intent.putExtra("profilePic",cursor.getBlob(cursor.getColumnIndex(contract_contacts.COLUMN_PIC)));
        intent.putExtra("name",cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_NAME)));
        intent.putExtra("number",cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_PH_NUMBER)));
        intent.putExtra("about",cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_ABOUT)));
        intent.putExtra("is_user",cursor.getInt(cursor.getColumnIndex(contract_contacts.COLUMN_IS_USER)));
        startActivity(intent);
    }

    @Override
    public void onImageClick(int position,Cursor cursor) {
        Intent intent=new Intent(getContext(), ProfileDetailsActivity.class);
        intent.putExtra("type","contact");
        intent.putExtra("image",cursor.getBlob(cursor.getColumnIndex(contract_contacts.COLUMN_PIC)));
        intent.putExtra("text1",cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_NAME)));
        intent.putExtra("text2",cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_PH_NUMBER)));
        intent.putExtra("text3",cursor.getString(cursor.getColumnIndex(contract_contacts.COLUMN_ABOUT)));
        startActivity(intent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getContext(),ChatContentProvider.CONTACTS_URI,
                new String[]{
                        contract_contacts.COLUMN_NAME,
                        contract_contacts.COLUMN_PIC,
                        contract_contacts.COLUMN_PIC_URL,
                        contract_contacts.COLUMN_PH_NUMBER,
                        contract_contacts.COLUMN_IS_USER,
                        contract_contacts.COLUMN_ABOUT,
                        contract_contacts.COLUMN_PIC_TIMESTAMP},
                null,null,contract_contacts.COLUMN_NAME+" COLLATE NOCASE ");
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
