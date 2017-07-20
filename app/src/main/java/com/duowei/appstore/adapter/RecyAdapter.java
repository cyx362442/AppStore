package com.duowei.appstore.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arialyy.aria.core.Aria;
import com.duowei.appstore.R;
import com.duowei.appstore.bean.LoadMsg;
import com.duowei.appstore.widget.HorizontalProgressBarWithNumber;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017-07-18.
 */

public class RecyAdapter extends RecyclerView.Adapter<RecyAdapter.ViewHold>{
    Context context;
    List<LoadMsg> mList;
    private int progress=0;
    private int index=-1;
    private boolean isLoad=false;
    private final LayoutInflater mLayoutInflater;
    private final String SDPATH = Environment.getExternalStorageDirectory() + "/DWstore";

    public RecyAdapter(Context context, List<LoadMsg> list) {
        this.context = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyDataSetChanged();
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public void setLoad(boolean load) {
        isLoad = load;
        notifyDataSetChanged();
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
        final LoadMsg loadMsg = mList.get(position);
        holder.mTextView.setText(loadMsg.name);
        if(index==position){
            holder.mProgress.setVisibility(View.VISIBLE);
            holder.mProgress.setProgress(progress);
        }else{
            holder.mProgress.setVisibility(View.GONE );
        }
        if(index!=position&&isLoad==true){
            holder.mButton.setEnabled(false);
            holder.mButton.setTextColor(context.getResources().getColor(R.color.gray));
        }else{
            holder.mButton.setEnabled(true);
            holder.mButton.setTextColor(context.getResources().getColor(R.color.blue_low));
        }
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index=position;
                File file = new File(SDPATH);
                if(!file.exists()){
                    file.mkdir();
                }
                Aria.download(context)
                        .load(loadMsg.url)
                        .setDownloadPath(SDPATH+loadMsg.name+".apk")
                        .start();
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
}
