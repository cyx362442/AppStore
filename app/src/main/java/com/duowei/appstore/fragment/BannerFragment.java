package com.duowei.appstore.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowei.appstore.R;
import com.duowei.appstore.bannerload.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerFragment extends Fragment implements OnBannerListener {
    public BannerFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_banner, container, false);
        Banner banner = inflate.findViewById(R.id.banner);
        banner.setDelayTime(5000);//轮播时间
        banner.setDrawingCacheBackgroundColor(getActivity().getResources().getColor(R.color.blue_low));
        String[] urls = getResources().getStringArray(R.array.url);
        List list = Arrays.asList(urls);
        List arrayList = new ArrayList(list);
        banner.setImages(arrayList)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerListener(BannerFragment.this)
                .start();
        return inflate;
    }

    //banner点击事件
    @Override
    public void OnBannerClick(int position) {

    }
}
