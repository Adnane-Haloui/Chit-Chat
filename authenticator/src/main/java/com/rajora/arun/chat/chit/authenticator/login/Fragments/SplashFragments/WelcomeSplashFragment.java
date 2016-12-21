package com.rajora.arun.chat.chit.authenticator.login.Fragments.SplashFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rajora.arun.chat.chit.authenticator.R;

public class WelcomeSplashFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public WelcomeSplashFragment() {
    }

    public static WelcomeSplashFragment newInstance() {
        WelcomeSplashFragment fragment = new WelcomeSplashFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_welcome_splash, container, false);
        view.findViewById(R.id.splash_get_started_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onGetStartedClick(v);
                }
            }
        });
        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onGetStartedClick(View view);
    }
}
