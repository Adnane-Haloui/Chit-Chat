package com.rajora.arun.chat.chit.chitchat.fragments;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.ContactsContract.RawContacts;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rajora.arun.chat.chit.chitchat.R.id;
import com.rajora.arun.chat.chit.chitchat.R.layout;
import com.rajora.arun.chat.chit.chitchat.R.menu;
import com.rajora.arun.chat.chit.chitchat.activities.AboutActivity;
import com.rajora.arun.chat.chit.chitchat.activities.ProfileEditActivity;
import com.rajora.arun.chat.chit.chitchat.R;
import com.rajora.arun.chat.chit.chitchat.services.FetchNewChatData;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class fragment_chat_lists extends ChatListenerFragment{

    private static final int TAG_ADD_CONTACT=100;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;
	private View mRootLayout;
    DatabaseReference connectedRef;
	private Snackbar mSnackbar;
	ConnectivityManager connectivityManager;

	private OnClickListener mSnackbarClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mSnackbar!=null && mSnackbar.isShownOrQueued()){
				mSnackbar.dismiss();
			}
		}
	};
	ValueEventListener mValueEventListener=new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			if(!dataSnapshot.getValue(Boolean.class)){
				if(mRootLayout!=null){
					if(mSnackbar!=null && mSnackbar.isShownOrQueued()){
						mSnackbar.dismiss();
					}
					NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
					if(activeNetwork!=null && !activeNetwork.isConnected()){
						mSnackbar=Snackbar.make(mRootLayout,"You are Offline!",Snackbar.LENGTH_INDEFINITE);
						mSnackbar.setAction("OK",mSnackbarClickListener);
						mSnackbar.show();
					}
				}
			}
			else{
				if(mSnackbar!=null && mSnackbar.isShownOrQueued()){
					mSnackbar.dismiss();
					mSnackbar=Snackbar.make(mRootLayout,"You are back Online!",Snackbar.LENGTH_SHORT);
					mSnackbar.show();
				}
			}

		}

		@Override
		public void onCancelled(DatabaseError databaseError) {

		}
	};

    public fragment_chat_lists() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        connectedRef=FirebaseDatabase.getInstance().getReference(".info/connected");
	    connectivityManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
    }

	@Override
	public void onResume() {
		super.onResume();
		connectedRef.addValueEventListener(mValueEventListener);
	}

	@Override
	public void onPause() {
		if(mSnackbar!=null && mSnackbar.isShownOrQueued()){
			mSnackbar.dismiss();
		}
		connectedRef.removeEventListener(mValueEventListener);
		super.onPause();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(layout.fragment_chat_lists, container, false);
		mViewPager= (ViewPager) view.findViewById(id.view_pager_chat_list);
        mViewPagerAdapter=new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mTabLayout= (TabLayout) view.findViewById(id.tabs_chat_list);

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(1);
        ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE))
                .cancelAll();
	    mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
		    @Override
		    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		    }

		    @Override
		    public void onPageSelected(int position) {
				if(position==1){
					((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE))
							.cancelAll();
				}
		    }

		    @Override
		    public void onPageScrollStateChanged(int state) {

		    }
	    });
		mRootLayout=view;
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case id.menu_edit_profile:
                Intent intent=new Intent(getActivity(),ProfileEditActivity.class);
                startActivity(intent);
                break;
            case id.menu_add_contact:
                Intent add_contact_intent = new Intent(Insert.ACTION);
                add_contact_intent.setType(RawContacts.CONTENT_TYPE);
                startActivityForResult(add_contact_intent,TAG_ADD_CONTACT);
                break;
            case id.menu_about:
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
           switch (position){
               case 0:return fragment_bot_list.newInstance();
               case 1:return fragment_chat_list.newInstance();
               case 2: return fragment_contact_list.newInstance();
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

}
