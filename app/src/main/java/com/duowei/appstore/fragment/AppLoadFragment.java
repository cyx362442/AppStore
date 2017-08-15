package com.duowei.appstore.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.duowei.appstore.R;
import com.duowei.appstore.adapter.RecyAdapter;
import com.duowei.appstore.adapter.SpaceItemDecoration;
import com.duowei.appstore.bean.LoadMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppLoadFragment extends Fragment {

    private RecyAdapter mAdapter;

    public AppLoadFragment() {
        // Required empty public constructor
    }
    List<LoadMsg>mList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_app_load, container, false);

        mList=new ArrayList<>();
        mList.add(new LoadMsg("/dw1","http://7xpj8w.com1.z0.glb.clouddn.com/video15.zip"));
        mList.add(new LoadMsg("/dw2","http://7xpj8w.com1.z0.glb.clouddn.com/app1.apk"));
        mList.add(new LoadMsg("/dw3","http://7xpj8w.com1.z0.glb.clouddn.com/app4.apk"));
        mList.add(new LoadMsg("/dw4","http://7xpj8w.com1.z0.glb.clouddn.com/app3.apk"));
        mList.add(new LoadMsg("/dw5","http://7xpj8w.com1.z0.glb.clouddn.com/app2.apk"));

        RecyclerView rv = inflate.findViewById(R.id.recycleView);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addItemDecoration(new SpaceItemDecoration(10));
        mAdapter = new RecyAdapter(getActivity(), mList);
        rv.setAdapter(mAdapter);
        return inflate;
    }

    //开始下载
    @Download.onTaskStart
    void taskStart(DownloadTask task) {
        mAdapter.setLoad(true);
        mAdapter.notifyDataSetChanged();
    }

    //下载中
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        long currentProgress = task.getCurrentProgress();
        long fileSize = task.getFileSize();
        int i = (int) ((currentProgress * 100) / fileSize);
        mAdapter.setProgress(i);
        mAdapter.notifyDataSetChanged();
    }

    //下载完成
    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        mAdapter.setIndex(-1);
        mAdapter.setLoad(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        Aria.download(AppLoadFragment.this).register();
    }

    @Override
    public void onStop() {
        super.onStop();
        Aria.download(AppLoadFragment.this).unRegister();
    }
}
