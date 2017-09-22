package com.duowei.appstore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.duowei.appstore.R;
import com.duowei.appstore.adapter.LVAdapter;
import com.duowei.appstore.bean.APKData;
import com.duowei.appstore.httputils.DownHTTP;
import com.duowei.appstore.httputils.VolleyResultListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class APKFragment extends Fragment {
    private final String url="http://owm0ww8l4.bkt.clouddn.com/appstoreJson.txt";
    private List<APKData> mList;
    private LVAdapter mLvAdapter;
    private ListView mLv;

    public APKFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_apk, container, false);
        mList=new ArrayList<>();

        mLv = inflate.findViewById(R.id.listView);
        mLvAdapter = new LVAdapter(mList,getActivity());
        mLv.setAdapter(mLvAdapter);
        Http_APk();
        return inflate;
    }
    private void Http_APk() {
        HashMap<String, String> map = new HashMap<>();
        DownHTTP.postVolley(url, map, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                String s = response.replaceAll("\n", "");
                mList.clear();
                List<APKData> list = gson.fromJson(s, new TypeToken<List<APKData>>() {
                }.getType());
                mList.addAll(list);
                mLvAdapter.notifyDataSetChanged();
            }
        });
    }

    //开始下载
    @Download.onTaskStart
    void taskStart(DownloadTask task) {
        mLvAdapter.notifyDataSetChanged();
    }

    //下载中
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        long currentProgress = task.getCurrentProgress();
        long fileSize = task.getFileSize();
        int i = (int) ((currentProgress * 100) / fileSize);
        mLvAdapter.setProgress(i);
        mLvAdapter.notifyDataSetChanged();
    }

    //下载完成
    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        mLvAdapter.setIndex(-1);
        mLvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        Aria.download(APKFragment.this).register();
    }

    @Override
    public void onStop() {
        super.onStop();
        Aria.download(APKFragment.this).unRegister();
    }
}
