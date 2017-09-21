package com.duowei.appstore.httputils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Administrator on 2016-12-16.
 */

public class VolleyUtils {
    private static final String TAG = "VolleyUtils";

    private static VolleyUtils mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private VolleyUtils(Context context) {
        mContext = context;

    }

    public static synchronized VolleyUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtils(context);
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

    public static void get(String url, Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(url, listener, errorListener);
        getInstance(mContext).addToRequestQueue(request);
    }

    public static void post(String url, final HashMap<String, String> map,
                            Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        getInstance(mContext).addToRequestQueue(request);
    }
}
