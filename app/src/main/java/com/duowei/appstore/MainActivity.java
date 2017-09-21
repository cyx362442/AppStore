package com.duowei.appstore;

import android.Manifest;
import android.os.Build;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.appstore.fragment.MainFragment;
import com.duowei.appstore.fragment.SettingFragment;
import com.duowei.appstore.util.OnPermissionCallback;
import com.duowei.appstore.util.PermissionManager;

public class MainActivity extends AppCompatActivity {
    private View indicator = null;
    private FragmentTabHost mTabHost;
    private int []title={R.string.main_title,R.string.setting_title};
    private int[]layout={R.layout.home_indicator,R.layout.setting_indicator};
    private Class[]cls={MainFragment.class,SettingFragment.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSD();
        mTabHost = (FragmentTabHost) findViewById(R.id.tablehost);
        initTabHost();
    }

    //检查内存是否可用
    private void checkSD() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            setEnable(true);
        } else {  //6.0处理
            boolean hasPermission = PermissionManager.getInstance()
                    .checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasPermission) {
//                setEnable(true);
            } else {
//                setEnable(false);
                PermissionManager.getInstance().requestPermission(this, new OnPermissionCallback() {
                    @Override public void onSuccess(String... permissions) {
//                        setEnable(true);
                    }
                    @Override public void onFail(String... permissions) {
                        Toast.makeText(MainActivity.this,"没有文件读写权限",Toast.LENGTH_SHORT).show();
//                        setEnable(false);
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void initTabHost() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        for(int i=0;i<title.length;i++){
            indicator = getIndicatorView(getResources().getString(title[i]),layout[i]);
            mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(title[i]))
                    .setIndicator(indicator),cls[i],null);
        }
    }

    private View getIndicatorView(String name, int layoutId) {
        View v = getLayoutInflater().inflate(layoutId, null);
        TextView tv = v.findViewById(R.id.tabText);
        tv.setText(name);
        return v;
    }
}
