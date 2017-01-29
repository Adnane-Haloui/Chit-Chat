package com.rajora.arun.chat.chit.authenticator.login;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rajora.arun.chat.chit.authenticator.R;
import com.rajora.arun.chat.chit.authenticator.login.Fragments.SplashFragments.LoginSplashFragment;
import com.rajora.arun.chat.chit.authenticator.login.Fragments.SplashFragments.PermissionsSplashFragment;
import com.rajora.arun.chat.chit.authenticator.login.Fragments.SplashFragments.WelcomeSplashFragment;

public class Login extends AppCompatActivity implements LoginSplashFragment.OnFragmentInteractionListener,
		PermissionsSplashFragment.OnFragmentInteractionListener, WelcomeSplashFragment.OnFragmentInteractionListener {


	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private TabLayout tabLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mViewPager = ((ViewPager) findViewById(R.id.view_pager_splash_screen));
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(mViewPagerAdapter);
		tabLayout = (TabLayout) findViewById(R.id.tabDots);
		tabLayout.setupWithViewPager(mViewPager, true);
		mViewPagerAdapter.notifyDataSetChanged();
		if (savedInstanceState != null && savedInstanceState.containsKey("pager-position")) {
			mViewPager.setCurrentItem(savedInstanceState.getInt("pager-position"));
		} else {
			mViewPager.setCurrentItem(0);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("pager-position", mViewPager.getCurrentItem());
	}

	@Override
	public void onLoginFinish() {
		finish();
	}

	@Override
	public void onGetStartedClick(View view) {
		mViewPager.setCurrentItem(1, true);
	}

	@Override
	public void onPermissionGranted() {
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
	}


	class ViewPagerAdapter extends FragmentPagerAdapter {


		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (android.os.Build.VERSION.SDK_INT >= 23) {
				switch (position) {
					case 0:
						return WelcomeSplashFragment.newInstance();
					case 1:
						return PermissionsSplashFragment.newInstance();
					case 2:
						return LoginSplashFragment.newInstance();
				}
			} else {
				switch (position) {
					case 0:
						return WelcomeSplashFragment.newInstance();
					case 1:
						return LoginSplashFragment.newInstance();
				}
			}
			return null;
		}

		@Override
		public int getCount() {
			return (android.os.Build.VERSION.SDK_INT >= 23) ? 3 : 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}
	}
}
