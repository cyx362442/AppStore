package com.duowei.appstore.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.duowei.appstore.R;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * A simple {@link} subclass.
 */
public class UpdateFragment extends DialogFragment {
    private ProgressBar mPb;
    private String SDPATH= Environment.getExternalStorageDirectory()+"/duowei/";
    private DownloadManager mDownloadManager;
    private long mEnqueue;
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private DownloadChangeObserver mObserver;
    private String mName;
    private DownloadManager.Request mRequest;
    public static UpdateFragment newInstance(String url,String name){
        UpdateFragment fragment = new UpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putString("name",name);
        fragment.setArguments(bundle);//把参数传递给该DialogFragment
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update, null);
        mPb = inflate.findViewById(R.id.pb);
        String url = getArguments().getString("url", "");
        mName = getArguments().getString("name", "");

        File file = new File(SDPATH);
        if(!file.exists()){
            file.mkdir();
        }
        deleteAllFiles(file);

        mRequest = new DownloadManager.Request(Uri.parse(url));
        //自定义下载保存目录
        mRequest.setDestinationUri(Uri.fromFile(new File(SDPATH+ mName)));
        mDownloadManager = (DownloadManager)getActivity().getSystemService(DOWNLOAD_SERVICE);
        mEnqueue = mDownloadManager.enqueue(mRequest);

        mObserver = new DownloadChangeObserver(null);
        getActivity().getContentResolver().registerContentObserver(CONTENT_URI, true, mObserver);
        return new AlertDialog.Builder(getActivity()).setView(inflate).create();
    }

    class DownloadChangeObserver extends ContentObserver {
        public DownloadChangeObserver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }
        @Override
        public void onChange(boolean selfChange) {
            queryDownloadStatus();
        }
    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mEnqueue);
        Cursor c = mDownloadManager.query(query);
        if(c!=null&&c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int fileSizeIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytesDLIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int fileSize = c.getInt(fileSizeIdx);
            int bytesDL = c.getInt(bytesDLIdx);
            final int progress = bytesDL * 100 / fileSize;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPb.setProgress(progress);
                }
            });
            // Display the status
            switch(status) {
                case DownloadManager.STATUS_PAUSED:
                    break;
                case DownloadManager.STATUS_PENDING:
                    break;
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //完成
                    Intent installintent = new Intent();
                    installintent.setAction(Intent.ACTION_VIEW);
                    // 在Boradcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
                    installintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    installintent.setDataAndType(Uri.fromFile(new File(SDPATH+mName)),
                            "application/vnd.android.package-archive");//存储位置为Android/data/包名/file/Download文件夹
                    getActivity().startActivity(installintent);
                    mRequest.setNotificationVisibility(View.GONE);
                    dismiss();
                    break;
                case DownloadManager.STATUS_FAILED:
                    //清除已下载的内容，重新下载
                    mDownloadManager.remove(mEnqueue);
                    dismiss();
                    break;
            }
        }
        c.close();
    }

    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }
}
