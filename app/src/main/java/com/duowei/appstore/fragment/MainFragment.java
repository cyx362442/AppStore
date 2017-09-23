package com.duowei.appstore.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arialyy.aria.core.Aria;
import com.duowei.appstore.R;
/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_main, container, false);
        Aria.download(MainFragment.this).setMaxSpeed(100.0);//下载限速
        Aria.get(MainFragment.this).getDownloadConfig().setMaxTaskNum(5);//允同时下载任务数量

        initFragment();
        return inflate;
    }

    private void initFragment() {
        BannerFragment bannerFragment = new BannerFragment();
        APKFragment apkFragment = new APKFragment();
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().replace(R.id.bannerFragment,bannerFragment).commit();
        fm.beginTransaction().replace(R.id.loadFragment,apkFragment).commit();
    }
}
