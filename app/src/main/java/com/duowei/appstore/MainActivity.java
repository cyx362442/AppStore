package com.duowei.appstore;

import android.Manifest;
import android.app.FragmentManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.duowei.appstore.fragment.AppLoadFragment;
import com.duowei.appstore.fragment.BannerFragment;
import com.duowei.appstore.util.OnPermissionCallback;
import com.duowei.appstore.util.PermissionManager;

public class MainActivity extends AppCompatActivity {

    private AppLoadFragment mLoadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Aria.download(this).setMaxSpeed(100.0);//下载限速
        Aria.get(this).getDownloadConfig().setMaxTaskNum(1);//允同时下载任务数量

        initFragment();

        checkSD();
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

    private void initFragment() {
        BannerFragment bannerFragment = new BannerFragment();
        mLoadFragment = new AppLoadFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame1,bannerFragment).commit();
        fragmentManager.beginTransaction().replace(R.id.frame2, mLoadFragment).commit();
    }
    //开始下载
    @Download.onTaskStart
    void taskStart(DownloadTask task) {
        mLoadFragment.setLoad(true);
    }
    //下载中
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        long currentProgress = task.getCurrentProgress();
        long fileSize = task.getFileSize();
        int i = (int) ((currentProgress * 100) / fileSize);
        mLoadFragment.setProgress(i);
    }
    //下载完成
    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        mLoadFragment.setIndex(-1);
        mLoadFragment.setLoad(false);
    }
}
