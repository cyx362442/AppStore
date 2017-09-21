package com.duowei.appstore.app;

import android.app.Application;
import android.content.Context;

import com.duowei.appstore.httputils.MyVolley;

/**
 * Created by Administrator on 2017-09-21.
 */

public class MyApp extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        MyVolley.init(this);
    }
    public static Context getContext(){
        return context;
    }
}
