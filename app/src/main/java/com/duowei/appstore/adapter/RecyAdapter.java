package com.duowei.appstore.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Administrator on 2017-07-18.
 */

public class RecyAdapter extends RecyclerView.Adapter<RecyAdapter.ViewHold>{
    Context context;
    List<APKData> mList;
    private int progress=0;
    private int index=-1;
    private final LayoutInflater mLayoutInflater;
    private final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DWstore/";

    public RecyAdapter(Context context, List<APKData> list) {
        this.context = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setList(List<APKData>list){
        this.mList=list;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.recycleview_item, parent,false);
        ViewHold viewHold = new ViewHold(inflate);
        viewHold.mImageView=inflate.findViewById(R.id.img);
        viewHold.mTextView=inflate.findViewById(R.id.tv_name);
        viewHold.mButton=inflate.findViewById(R.id.btn_load);
        viewHold.mProgress=inflate.findViewById(R.id.progress);
        return viewHold;
    }

    @Override
    public void onBindViewHolder(final ViewHold holder, final int position) {
        final APKData apkData = mList.get(position);
        holder.mTextView.setText(apkData.getAppName());
        Glide.with(context).load(apkData.getImgUrl()).fitCenter().into(holder.mImageView);

        if(index!=-1){//存在下载的APK
            if(apkData.isLoad()==true){//下载中的那个APK
                holder.mButton.setEnabled(true);
                holder.mButton.setTextColor(context.getResources().getColor(R.color.blue_low));
                holder.mButton.setText(context.getResources().getString(R.string.cancel));

                holder.mProgress.setVisibility(View.VISIBLE);
                holder.mProgress.setProgress(progress);
            }else{//不在下载中的APK,等待中^
                holder.mButton.setEnabled(false);
                holder.mProgress.setVisibility(View.GONE );
                holder.mButton.setTextColor(context.getResources().getColor(R.color.gray));
                if(fileIsExists(apkData.getApkName())){//文件夹中己存此APK安装包
                    holder.mButton.setText(context.getResources().getString(R.string.install));
                }else{
                    holder.mButton.setText(context.getResources().getString(R.string.load));
                }
            }
        }else{//正常状态
            holder.mButton.setEnabled(true);
            holder.mProgress.setVisibility(View.GONE );
            holder.mButton.setTextColor(context.getResources().getColor(R.color.blue_low));
            if(fileIsExists(apkData.getApkName())){//文件夹中己存此APK安装包
                holder.mButton.setText(context.getResources().getString(R.string.install));
            }else{
                holder.mButton.setText(context.getResources().getString(R.string.load));
            }
        }
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index=position;
                File file = new File(SDPATH);
                if(!file.exists()){
                    file.mkdir();
                }
                if(holder.mButton.getText().toString().equals(context.getResources().getString(R.string.load))){
                    Aria.download(context)
                            .load(apkData.getApkUrl())
                            .setDownloadPath(SDPATH+apkData.getApkName())
                            .start();
                    index=position;
                    for(int i=0;i<mList.size();i++){
                        mList.get(i).setLoad(false);
                    }
                    apkData.setLoad(true);
                }else if(holder.mButton.getText().toString().equals(context.getResources().getString(R.string.cancel))){
                    Aria.download(context)
                            .load(apkData.getApkUrl())
                            .cancel();
                    index=-1;
                    for(int i=0;i<mList.size();i++){
                        mList.get(i).setLoad(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHold extends RecyclerView.ViewHolder{
        public ViewHold(View itemView) {
            super(itemView);
        }
        ImageView mImageView;
        TextView mTextView;
        Button mButton;
        HorizontalProgressBarWithNumber mProgress;
    }

    public boolean fileIsExists(String appName){
        try{
            File f=new File(SDPATH+appName);
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
