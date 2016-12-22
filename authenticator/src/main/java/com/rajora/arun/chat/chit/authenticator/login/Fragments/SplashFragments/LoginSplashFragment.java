package com.rajora.arun.chat.chit.authenticator.login.Fragments.SplashFragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.rajora.arun.chat.chit.authenticator.BuildConfig;
import com.rajora.arun.chat.chit.authenticator.R;
import com.rajora.arun.chat.chit.authenticator.login.Login;
import com.rajora.arun.chat.chit.authenticator.login.User_Metadata;
import com.rajora.arun.chat.chit.authenticator.login.Volley.VolleySingletonRequestQueue;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static android.content.Context.MODE_PRIVATE;

public class LoginSplashFragment extends Fragment {

    private enum Auth_Status{AUTHENTICATE_ON_DIGIT,FETCH_TOKEN_FROM_AZURE,AUTHENTICATE_ON_FIREBASE};

    private static final String TOKEN_URL= BuildConfig.TOKEN_URL;
    private static final String TWITTER_KEY = BuildConfig.TWITTER_KEY;
    private static final String TWITTER_SECRET = BuildConfig.TWITTER_SECRET;

    private final View.OnClickListener verifyOnDigitsClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            authenticate();
        }
    };
    private final View.OnClickListener fetchFirebaseAuthTokenClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoginButton.setVisibility(View.GONE);
            mErrorMessageTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            makeFirebaseTokenRequest();
        }
    };
    private final View.OnClickListener authOnFirebaseAuthClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoginButton.setVisibility(View.GONE);
            mErrorMessageTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            makeFirebaseAuth();
        }
    };



    private Auth_Status mCurrentAuthStatus;
    private String mPhoneNum;
    private String mDigitAuthToken;
    private String mDigitAuthSecret;
    private String mFirebaseAuthToken;

    private OnFragmentInteractionListener mListener;
    private Button mLoginButton;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;
    private final AuthCallback callback=new AuthCallback() {
        @Override
        public void success(final DigitsSession session, final String phoneNumber) {
            mDigitAuthToken=session.getAuthToken().token;
            mDigitAuthSecret=session.getAuthToken().secret;
            mPhoneNum=phoneNumber;
            mCurrentAuthStatus=Auth_Status.FETCH_TOKEN_FROM_AZURE;

            SharedPreferences.Editor editor=getContext().getSharedPreferences("user-details",MODE_PRIVATE).edit();
            editor.putString("phone",phoneNumber);
            editor.commit();

            onDigitVerification();
        }
        @Override
        public void failure(DigitsException exception) {
            onDigitVerificationFailed();
        }
    };

    public LoginSplashFragment() {
    }

    public static LoginSplashFragment newInstance() {
        return new LoginSplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null && savedInstanceState.containsKey("phoneNumber")){
            mPhoneNum=savedInstanceState.getString("phoneNumber");
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey("currentAuthState")){
            mCurrentAuthStatus=(Auth_Status) savedInstanceState.get("currentAuthState");
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey("mDigitAuthToken") && savedInstanceState.containsKey("mDigitAuthSecret")){
            mDigitAuthToken=savedInstanceState.getString("mDigitAuthToken");
            mDigitAuthSecret=savedInstanceState.getString("mDigitAuthSecret");
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey("firebaseToken")){
            mFirebaseAuthToken=savedInstanceState.getString("firebaseToken");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_login_splash, container, false);
        mLoginButton=(Button)view.findViewById(R.id.splash_login_button);
        mErrorMessageTextView=(TextView)view.findViewById(R.id.splash_login_error_message_textview);
        mProgressBar=(ProgressBar)view.findViewById(R.id.splash_login_progressbar);
        if(savedInstanceState!=null && savedInstanceState.containsKey("errorMessage")){
            mErrorMessageTextView.setText(savedInstanceState.getString("errorMessage"));
            mErrorMessageTextView.setVisibility(View.VISIBLE);
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey("buttonText")){
            mLoginButton.setText(savedInstanceState.getString("buttonText"));
        }
        if(mCurrentAuthStatus!=null){
            switch (mCurrentAuthStatus){
                case AUTHENTICATE_ON_DIGIT: mLoginButton.setOnClickListener(verifyOnDigitsClickListener);
                    break;
                case FETCH_TOKEN_FROM_AZURE: mLoginButton.setOnClickListener(fetchFirebaseAuthTokenClickListener);
                    break;
                case AUTHENTICATE_ON_FIREBASE: mLoginButton.setOnClickListener(authOnFirebaseAuthClickListener);
                    break;
                default: mLoginButton.setOnClickListener(verifyOnDigitsClickListener);
                    break;
            }
        }
        else{
            mLoginButton.setOnClickListener(verifyOnDigitsClickListener);
        }

        if(savedInstanceState!=null && savedInstanceState.containsKey("buttonVisiblity")
                && savedInstanceState.getString("buttonVisiblity")=="GONE"){
            mLoginButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mLoginButton.callOnClick();
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

    public interface OnFragmentInteractionListener {
        void onLoginFinish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mPhoneNum!=null){
            outState.putString("phoneNumber",mPhoneNum);
        }
        if(mCurrentAuthStatus!=null){
            outState.putSerializable("currentAuthState",mCurrentAuthStatus);
        }
        if(mErrorMessageTextView!=null){
            outState.putString("errorMessage",mErrorMessageTextView.getText().toString());
        }
        if(mLoginButton!=null){
            outState.putString("buttonText",mLoginButton.getText().toString());
        }
        if(mDigitAuthSecret!=null && mDigitAuthToken!=null){
            outState.putString("mDigitAuthToken",mDigitAuthToken);
            outState.putString("mDigitAuthSecret",mDigitAuthSecret);
        }
        if(mFirebaseAuthToken!=null){
            outState.putString("firebaseToken",mFirebaseAuthToken);
        }
        if(mLoginButton!=null){
            outState.putString("buttonVisiblity",mLoginButton.getVisibility()==View.GONE?"GONE":"VISIBLE");
        }
    }

    private void showErrorMessage(String s)
    {
        mErrorMessageTextView.setText(s);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void onDigitVerification(){
        mProgressBar.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.GONE);
        mErrorMessageTextView.setVisibility(View.GONE);
        makeFirebaseTokenRequest();
    }

    private void makeFirebaseTokenRequest(){
        Map<String,String> params = new HashMap<String, String>();
        params.put("number",mPhoneNum.substring(1));
        params.put("token",mDigitAuthToken);
        params.put("secret", mDigitAuthSecret);
        JSONObject jsonParams=new JSONObject(params);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, TOKEN_URL,
                jsonParams,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final Intent intent_result=new Intent();
                        intent_result.putExtra(User_Metadata.O_AUTH_TOKEN,mDigitAuthToken);
                        intent_result.putExtra(User_Metadata.O_AUTH_SECRET,mDigitAuthSecret);
                        intent_result.putExtra(User_Metadata.PHONE_NUMBER,mPhoneNum);
                        boolean errorOccurred=false;
                        try {
                            if(response.getString("token").equals("auth_failed")){
                                errorOccurred=true;
                                showErrorMessage("Our servers are under maintenance. Please try again later!");
                                mLoginButton.setText("TRY AGAIN");
                                mProgressBar.setVisibility(View.GONE);
                                mLoginButton.setOnClickListener(fetchFirebaseAuthTokenClickListener);
                                mLoginButton.setVisibility(View.VISIBLE);
                            }
                            else{
                                mFirebaseAuthToken=response.getString("token");
                                intent_result.putExtra(User_Metadata.FIREBASE_TOKEN,response.getString("token"));
                                SharedPreferences.Editor editor=getContext().getSharedPreferences("user-details",MODE_PRIVATE).edit();
                                editor.putString("firebase_auth_token",response.getString("token"));
                                if(response.has("name")){
                                    editor.putString("name",response.getString("name"));
                                }
                                if(response.has("about")){
                                    editor.putString("about",response.getString("about"));
                                }
                                if(response.has("profile_pic_timestamp")){
                                    editor.putString("profile_pic_timestamp",response.getString("profile_pic_timestamp"));
                                }
                                editor.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorOccurred=true;
                            showErrorMessage("Please check network Connectivity!");
                            mLoginButton.setText("TRY AGAIN");
                            mProgressBar.setVisibility(View.GONE);
                            mLoginButton.setOnClickListener(fetchFirebaseAuthTokenClickListener);
                            mLoginButton.setVisibility(View.VISIBLE);
                        }
                        if(!errorOccurred){
                            getActivity().setResult(Activity.RESULT_OK,intent_result);
                            mCurrentAuthStatus=Auth_Status.AUTHENTICATE_ON_FIREBASE;
                            makeFirebaseAuth();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMessage("Please check network Connectivity!");
                mLoginButton.setText("TRY AGAIN");
                mProgressBar.setVisibility(View.GONE);
                mLoginButton.setOnClickListener(fetchFirebaseAuthTokenClickListener);
                mLoginButton.setVisibility(View.VISIBLE);
            }
        });
        VolleySingletonRequestQueue.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void makeFirebaseAuth(){
        (FirebaseAuth.getInstance()).signInWithCustomToken(mFirebaseAuthToken)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){

                            Log.d("findme",task.getException().getMessage().toString());
                            Toast.makeText(getContext(),"Auth failed on firebase",Toast.LENGTH_SHORT).show();
                            showErrorMessage("Please check network Connectivity!");
                            mLoginButton.setText("TRY AGAIN");
                            mProgressBar.setVisibility(View.GONE);
                            mLoginButton.setOnClickListener(authOnFirebaseAuthClickListener);
                            mLoginButton.setVisibility(View.VISIBLE);
                        }
                        else{

                            SharedPreferences.Editor editor=getContext().getSharedPreferences("user-details",MODE_PRIVATE).edit();
                            editor.putString("login_status","complete");
                            editor.commit();

                            mListener.onLoginFinish();
                        }
                    }
                });
    }

    private void onDigitVerificationFailed(){
        showErrorMessage("Phone number verification failed. Please try after sometime later.");
    }
    public void authenticate(){
        mCurrentAuthStatus=Auth_Status.AUTHENTICATE_ON_DIGIT;
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getContext(), new TwitterCore(authConfig), new Digits.Builder().build());
        AuthConfig.Builder mAuthConfig=new AuthConfig.Builder()
                .withAuthCallBack(callback);
        Digits.authenticate(mAuthConfig.build());
    }
}
