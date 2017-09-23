package com.duowei.appstore.app;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.duowei.appstore.R;
import com.duowei.appstore.fragment.MainFragment;
import com.duowei.appstore.fragment.SettingFragment;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
    private TabHost mTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTabs();
    }
    // 初始化标签按钮
    private void setupTabs() {
        mTabHost = findViewById(R.id.tabhost);
        mTabHost.setup();
        // 生成底部自定义样式的按钮
        Class[]cls={MainFragment.class,SettingFragment.class};
        String[] title = new String[] { "主页", "设置"};
        int[] tabIds = new int[] { R.id.tab1, R.id.tab2};
        int[]layout={R.layout.home_indicator,R.layout.setting_indicator};
        for (int i = 0; i < title.length; i++) {
            View indicatorView = getIndicatorView(title[i], layout[i]);
            mTabHost.addTab(mTabHost.newTabSpec(title[i]).setIndicator(indicatorView)
                    .setContent(tabIds[i]));
        }
        initFragment("主页");
        mTabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onTabChanged(String tag) {
        initFragment(tag);
    }

    private void initFragment(String tag) {
        Fragment frag = null;
        int contentViewID = 0;
        if (tag.equals("主页")) {
            frag = new MainFragment(); //自定义继承Fragment的UI，放了一个简单的显示文本标题的控件。
            contentViewID = R.id.tab1;
        } else if (tag.equals("设置")) {
            frag = new SettingFragment();
            contentViewID = R.id.tab2;
        }
        if (frag == null)
            return;
        android.app.FragmentManager manager = getFragmentManager();
        if (manager.findFragmentByTag(tag) == null) {
            android.app.FragmentTransaction ft = manager.beginTransaction();
            ft.replace(contentViewID, frag, tag);
            ft.commit();
        }
    }

    private View getIndicatorView(String name, int layoutId) {
        View v = getLayoutInflater().inflate(layoutId, null);
        TextView tv = v.findViewById(R.id.tabText);
        tv.setText(name);
        return v;
    }
}
