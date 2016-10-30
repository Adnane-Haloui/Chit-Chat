package com.rajora.arun.chat.chit.chitchat.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rajora.arun.chat.chit.chitchat.activities.AboutActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileEditActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;

public class fragment_chat_lists extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;
    public fragment_chat_lists() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    FetchNewChatData mBoundService;
    boolean service_connected=false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service_connected=false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            service_connected=true;
            FetchNewChatData.customBinder myBinder = (FetchNewChatData.customBinder) service;
            mBoundService = myBinder.getService();
            mBoundService.setCurrentItemId(null);
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat_lists, container, false);
            mViewPager= ((ViewPager) view.findViewById(R.id.view_pager_chat_list));
        mViewPagerAdapter=new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mTabLayout= ((TabLayout) view.findViewById(R.id.tabs_chat_list));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(1);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit_profile:
                Intent intent=new Intent(getActivity(),ProfileEditActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_add_contact:
                Intent add_contact_intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                add_contact_intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(add_contact_intent);
                break;
            case R.id.menu_about:
                Intent about_intent=new Intent(getActivity(),AboutActivity.class);
                startActivity(about_intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter{


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.d("findme","got position "+position);
           switch (position){
               case 0:return fragment_bot_list.newInstance(0);
               case 1:return fragment_chat_list.newInstance(1);
               case 2: return fragment_contact_list.newInstance(2);
           }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0:return "Bots";
                case 1:return "Chats";
                case 2: return "Contacts";
            }
            return "";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), FetchNewChatData.class);
        getContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        if(service_connected )
            getContext().unbindService(mServiceConnection);
        super.onPause();
    }
}
