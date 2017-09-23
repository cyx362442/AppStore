package com.duowei.appstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.arialyy.aria.core.Aria;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.duowei.appstore.R;
import com.duowei.appstore.app.MyApp;
import com.duowei.appstore.bean.APKData;
import com.duowei.appstore.util.ToastUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017-09-23.
 */

public class RcyAdapter extends BaseQuickAdapter<APKData>{
    private final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"/appstore";
    private final Context mContext;
    private List<APKData>mList;
    private int mProgress=0;
    private static int index=-1;
    public RcyAdapter(List<APKData> data) {
        super(R.layout.listview_item, data);
        this.mList=data;
        mContext = MyApp.getContext();
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }
    public void setIndex(int index) {
        this.index=index;
    }
    public  int getIndex() {
        return index;
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final APKData apkData) {
        //初始化各控件
        baseViewHolder.setText(R.id.tv_name,apkData.getAppName());
        baseViewHolder.setText(R.id.tv_content,"描述:"+apkData.getContent());
        baseViewHolder.setText(R.id.tv_version,"版本:"+apkData.getVersionName());
        Glide.with(mContext).load(apkData.getImgUrl()).crossFade().
                into((ImageView) baseViewHolder.getView(R.id.img));
        if (index != -1) {//存在下载的APK
            if (apkData.isLoad() == true) {//下载中的那个APK
                baseViewHolder.getView(R.id.btn_load).setEnabled(true);
                baseViewHolder.setTextColor(R.id.btn_load, Color.parseColor("#19BEF3"));
                baseViewHolder.setText(R.id.btn_load,mContext.getString(R.string.cancel));
                baseViewHolder.getView(R.id.progress).setVisibility(View.VISIBLE);
                baseViewHolder.setProgress(R.id.progress,mProgress);
            } else {//不在下载中的APK,等待中^
                baseViewHolder.getView(R.id.btn_load).setEnabled(false);
                baseViewHolder.setTextColor(R.id.btn_load, Color.parseColor("#F0F0F0"));
                baseViewHolder.getView(R.id.progress).setVisibility(View.GONE);
                if (fileIsExists(apkData.getApkName())) {//文件夹中己存此APK安装包
                    baseViewHolder.setText(R.id.btn_load,mContext.getString(R.string.install));
                } else {
                    baseViewHolder.setText(R.id.btn_load,mContext.getString(R.string.load));
                }
            }
        } else {//正常状态
            baseViewHolder.getView(R.id.btn_load).setEnabled(true);
            baseViewHolder.setTextColor(R.id.btn_load, Color.parseColor("#19BEF3"));
            baseViewHolder.getView(R.id.progress).setVisibility(View.GONE);
            if (fileIsExists(apkData.getApkName())&&isAvilible(apkData.getPackageName())==false) {//文件夹中己存此APK安装包
                baseViewHolder.setText(R.id.btn_load,mContext.getString(R.string.install));
            } else if(isAvilible(apkData.getPackageName())){//己安装了此APP
                baseViewHolder.setText(R.id.btn_load,mContext.getString(R.string.open));
            }else {
                baseViewHolder.setText(R.id.btn_load,mContext.getString(R.string.load));
            }
        }

        //下载点击事件
        final Button btnLoad = baseViewHolder.getView(R.id.btn_load);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(SDPATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String btnText = btnLoad.getText().toString();
                if (btnText.equals(mContext.getString(R.string.load))) {
                    Aria.download(mContext)
                            .load(apkData.getApkUrl())
                            .setDownloadPath(SDPATH + "/" + apkData.getApkName())
                            .start();
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setLoad(false);
                    }
                    apkData.setLoad(true);
                    index = baseViewHolder.getAdapterPosition();
                } else if (btnText.equals(mContext.getString(R.string.cancel))) {
                    Aria.download(mContext)
                            .load(apkData.getApkUrl())
                            .cancel();
                    index = -1;
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setLoad(false);
                    }
                } else if (btnText.equals(mContext.getString(R.string.install))) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    // 由于没有在Activity环境下启动Activity,设置下面的标签
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(
                            new File(SDPATH, "/" + apkData.getApkName())),
                            "application/vnd.android.package-archive");
                    mContext.startActivity(intent);
                }else if(btnText.equals(mContext.getString(R.string.open))){
                    startAPP(apkData.getPackageName());
                }
                notifyDataSetChanged();
            }
        });
    }
    public boolean fileIsExists(String appName){
        try{
            File f=new File(SDPATH+"/"+appName);
            if(!f.exists()){
                return false;
            }
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
    public boolean isAvilible(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();

        //获取手机系统的所有APP包名，然后进行一一比较
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
    public void startAPP(String appPackageName){
        try{
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(appPackageName);
            mContext.startActivity(intent);
        }catch(Exception e){
            ToastUtil.showToast("打开失败");
        }
    }
}
