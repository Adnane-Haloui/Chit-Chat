package com.rajora.arun.chat.chit.authenticator.login.Fragments.SplashFragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rajora.arun.chat.chit.authenticator.R;

public class PermissionsSplashFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Button askPermissionButton;
    public PermissionsSplashFragment() {
    }

    public static PermissionsSplashFragment newInstance() {
        return new PermissionsSplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_permissions_splash, container, false);
        askPermissionButton=(Button)view.findViewById(R.id.splash_permission_button);
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            askPermissionButton.setText("Permissions Granted!");
            askPermissionButton.setContentDescription("Permissions Granted");
            askPermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPermissionGranted();
                }
            });
        }
        else {
            askPermissionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askPermission();
                }
            });
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            setPermissionGranted();
        }
    }

    public boolean askPermission(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE},100);
            return false;
        }
        else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS},200);
            return false;
        }
        else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE},300);
            return false;
        }
        else{
            setPermissionGranted();
            return true;
        }
    }

    private void setPermissionGranted(){
        askPermissionButton.setText("Permissions Granted!");
        askPermissionButton.setContentDescription("Permissions Granted");
        askPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPermissionGranted();
            }
        });
        if(mListener!=null){
            mListener.onPermissionGranted();
        }
    }

    public interface OnFragmentInteractionListener {
        void onPermissionGranted();
    }
}
