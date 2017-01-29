package com.rajora.arun.chat.chit.authenticator.login.Volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by arc on 21/12/16.
 */

public class VolleySingletonRequestQueue {
	private static VolleySingletonRequestQueue mInstance;
	private static Context mContext;
	private RequestQueue mRequestQueue;

	private VolleySingletonRequestQueue(Context context) {
		mContext = context;
		mRequestQueue = getRequestQueue();
	}

	public static synchronized VolleySingletonRequestQueue getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new VolleySingletonRequestQueue(context);
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

}
