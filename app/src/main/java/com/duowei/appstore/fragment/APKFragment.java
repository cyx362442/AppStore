package com.duowei.appstore.fragment;

import android.app.Fragment;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.duowei.appstore.R;
import com.duowei.appstore.adapter.RcyAdapter;
import com.duowei.appstore.adapter.SpaceItemDecoration;
import com.duowei.appstore.bean.APKData;
import com.duowei.appstore.httputils.DownHTTP;
import com.duowei.appstore.httputils.VolleyResultListener;
import com.duowei.appstore.util.ToastUtil;
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
    private RcyAdapter mRcyAdapter;
    private LinearLayout mLlLoad;

    public APKFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_apk, container, false);
        mList=new ArrayList<>();
        mLlLoad = inflate.findViewById(R.id.ll_load);
        RecyclerView rv = inflate.findViewById(R.id.recycleView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addItemDecoration(new SpaceItemDecoration(5));

        mRcyAdapter = new RcyAdapter(mList);
        rv.setAdapter(mRcyAdapter);
        Http_APk();
        Aria.download(this).addSchedulerListener(new MySchedulerListener());
        return inflate;
    }
    private void Http_APk() {
        HashMap<String, String> map = new HashMap<>();
        DownHTTP.postVolley(url, map, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLlLoad.setVisibility(View.GONE);
                ToastUtil.showToast("加载失败");
            }
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                String s = response.replaceAll("\n", "");
                mList.clear();
                List<APKData> list = gson.fromJson(s, new TypeToken<List<APKData>>() {
                }.getType());
                mList.addAll(list);
                mRcyAdapter.notifyDataSetChanged();
                mLlLoad.setVisibility(View.GONE);
            }
        });
    }

    private class MySchedulerListener extends Aria.DownloadSchedulerListener {
        @Override public void onTaskStart(DownloadTask task) {
        }
        @Override public void onTaskStop(DownloadTask task) {
        }
        @Override public void onTaskCancel(DownloadTask task) {
            Aria.download(this).removeAllTask();
        }
        @Override public void onTaskFail(DownloadTask task) {
//            ToastUtil.showToast("下载失败");
//            mRcyAdapter.setIndex(-1);
//            mRcyAdapter.notifyDataSetChanged();
        }
        @Override public void onTaskComplete(DownloadTask task) {
            mRcyAdapter.setIndex(-1);
//            mRcyAdapter.notifyDataSetChanged();
            mRcyAdapter.InstallApk();
        }
        @Override public void onTaskRunning(DownloadTask task) {
            long currentProgress = task.getCurrentProgress();
            long fileSize = task.getFileSize();
            int i = (int) ((currentProgress * 100) / fileSize);
            mRcyAdapter.setProgress(i);
            mRcyAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mRcyAdapter.setIndex(-1);
    }
}