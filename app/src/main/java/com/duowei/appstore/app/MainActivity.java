package com.duowei.appstore.app;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.duowei.appstore.R;
import com.duowei.appstore.event.FinishEvent;
import com.duowei.appstore.fragment.MainFragment;
import com.duowei.appstore.fragment.SettingFragment;
import com.duowei.appstore.fragment.UpdateFragment;
import com.duowei.appstore.httputils.DownHTTP;
import com.duowei.appstore.httputils.VolleyResultListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener {

    private static final String urlUpdate="http://owm0ww8l4.bkt.clouddn.com/appstore.txt";
    private static int mVersionCode;
    private TabHost mTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        setupTabs();
        getAPPVersionName();
        checkVersion();

        hideBar();
    }

    @Subscribe
    public void finishActivity(FinishEvent evnet){
        finish();
    }

    private void hideBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private void checkVersion() {
        DownHTTP.getVolley(urlUpdate, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String versionCode = jsonObject.getString("versionCode");
                    String msg = jsonObject.getString("msg");
                    String url = jsonObject.getString("url");
                    String name = jsonObject.getString("name");
                    if(Integer.parseInt(versionCode)>mVersionCode){
                        showUpdateDialog(msg, url, name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showUpdateDialog(String msg, final String url, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.mipmap.logo96);
        builder.setTitle("发现新版本，是否升级？");
        builder.setMessage(msg);
        builder.setNegativeButton("暂不升级",null);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateFragment updateFragment = UpdateFragment.newInstance(url,name);
                updateFragment.show(getFragmentManager(),getString(R.string.update));
            }
        });
        builder.show();
    }

    // 初始化标签按钮
    private void setupTabs() {
        mTabHost = findViewById(R.id.tabhost);
        mTabHost.setup();
        // 生成底部自定义样式的按钮
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

    //当前APP版本号
    public void getAPPVersionName() {
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            mVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
