package com.duowei.appstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arialyy.aria.core.Aria;
import com.bumptech.glide.Glide;
import com.duowei.appstore.R;
import com.duowei.appstore.bean.APKData;
import com.duowei.appstore.widget.HorizontalProgressBarWithNumber;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017-09-22.
 */

public class LVAdapter extends BaseAdapter {
    private List<APKData> mList;
    private Context context;
    private int progress=0;
    private static int index=-1;
    private LayoutInflater mLayoutInflater;
    private final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/DWstore";

    public LVAdapter(List<APKData> list, Context context) {
        mList = list;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
    public void setIndex(int index) {
        this.index=index;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHold hold;
        if (convertView == null) {
            hold = new ViewHold();
            convertView = mLayoutInflater.inflate(R.layout.listview_item, null);
            hold.mImageView = convertView.findViewById(R.id.img);
            hold.mTextView = convertView.findViewById(R.id.tv_name);
            hold.mButton = convertView.findViewById(R.id.btn_load);
            hold.mProgress = convertView.findViewById(R.id.progress);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }
        final APKData apkData = mList.get(position);
        hold.mTextView.setText(apkData.getAppName());
        Glide.with(context).load(apkData.getImgUrl()).fitCenter().into(hold.mImageView);

        if (index != -1) {//存在下载的APK
            if (apkData.isLoad() == true) {//下载中的那个APK
                hold.mButton.setEnabled(true);
                hold.mButton.setTextColor(context.getResources().getColor(R.color.blue_low));
                hold.mButton.setText(context.getResources().getString(R.string.cancel));
                hold.mProgress.setVisibility(View.VISIBLE);
                hold.mProgress.setProgress(progress);
                apkData.setPro(progress + 1);
            } else {//不在下载中的APK,等待中^
                hold.mButton.setEnabled(false);
                hold.mProgress.setVisibility(View.GONE);
                hold.mButton.setTextColor(context.getResources().getColor(R.color.gray));
                if (fileIsExists(apkData.getApkName())) {//文件夹中己存此APK安装包
                    hold.mButton.setText(context.getResources().getString(R.string.install));
                } else {
                    hold.mButton.setText(context.getResources().getString(R.string.load));
                }
            }
        } else {//正常状态
            hold.mButton.setEnabled(true);
            hold.mProgress.setVisibility(View.GONE);
            hold.mButton.setTextColor(context.getResources().getColor(R.color.blue_low));
            if (fileIsExists(apkData.getApkName())) {//文件夹中己存此APK安装包
                hold.mButton.setText(context.getResources().getString(R.string.install));
            } else {
                hold.mButton.setText(context.getResources().getString(R.string.load));
            }
        }
        final ViewHold finalViewHold = hold;
        hold.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(SDPATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (finalViewHold.mButton.getText().toString().equals(context.getResources().getString(R.string.load))) {
                    Aria.download(context)
                            .load(apkData.getApkUrl())
                            .setDownloadPath(SDPATH + "/" + apkData.getApkName())
                            .start();
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setLoad(false);
                    }
                    apkData.setLoad(true);
                    index = position;
                } else if (finalViewHold.mButton.getText().toString().equals(context.getResources().getString(R.string.cancel))) {
                    Aria.download(context)
                            .load(apkData.getApkUrl())
                            .cancel();
                    index = -1;
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setLoad(false);
                    }
                } else if (finalViewHold.mButton.getText().toString().equals(context.getResources().getString(R.string.install))) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    // 由于没有在Activity环境下启动Activity,设置下面的标签
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(
                            new File(SDPATH, "/" + apkData.getApkName())),
                            "application/vnd.android.package-archive");
                    context.startActivity(intent);
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    class ViewHold {
        ImageView mImageView;
        TextView mTextView;
        Button mButton;
        HorizontalProgressBarWithNumber mProgress;
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
}
