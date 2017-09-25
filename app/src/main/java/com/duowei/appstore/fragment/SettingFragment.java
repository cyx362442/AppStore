package com.duowei.appstore.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.duowei.appstore.R;
import com.duowei.appstore.httputils.DownHTTP;
import com.duowei.appstore.httputils.VolleyResultListener;
import com.duowei.appstore.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private static final String urlUpdate="http://owm0ww8l4.bkt.clouddn.com/appstore.txt";
    private static final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static String mVersionName;
    private static int mVersionCode;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_setting, container, false);
        getAPPVersionName();

        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().replace(R.id.frame,new BaseFragment()).commit();
        return inflate;
    }
    public static class BaseFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        private File mFile;
        private Preference mPreference;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            mPreference = findPreference(getString(R.string.clear));
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.service));
            Preference preference2 = findPreference(getString(R.string.version));
            Preference preferenceExit = findPreference(getString(R.string.exit));
            mFile = new File(SDPATH,"/appstore/");
            mPreference.setSummary("大小："+getFileSizes(mFile)/1024/1024+"M");
            preference2.setSummary(mVersionName);
            mPreference.setOnPreferenceClickListener(this);
            listPreference.setOnPreferenceClickListener(this);
            preference2.setOnPreferenceClickListener(this);
            preferenceExit.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.getKey()==getString(R.string.clear)){
                if (deleteFile()) return false;
                mPreference.setSummary("大小："+getFileSizes(mFile)/1024/1024+"M");
            }else if(preference.getKey()==getString(R.string.version)){
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
                                showUpdatDialog(msg, url, name);
                            }else{
                                ToastUtil.showToast("当前己是最新版");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else if(preference.getKey()==getString(R.string.exit)){
                getActivity().finish();
            }
            return false;
        }

        private void showUpdatDialog(String msg, final String url, final String name) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.mipmap.logo96);
            builder.setMessage(msg);
            builder.setTitle("发现新版本，是否升级？");
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

        private boolean deleteFile() {
            if(!mFile.exists()){
                return true;
            }
            File[] files = mFile.listFiles();
            for(int i=0;i<files.length;i++){
                if(files[i].exists()){
                    files[i].delete();
                }
            }
            return false;
        }
    }
    //当前APP版本号
    public void getAPPVersionName() {
        PackageManager manager = getActivity().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            // 版本名
            mVersionName = info.versionName;
            mVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定文件夹
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f){
        if(!f.exists()){
            return 0;
        }
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++){
            if (flist[i].isDirectory()){
                size = size + getFileSizes(flist[i]);
            }
            else{
                try {
                    size =size + getFileSize(flist[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }
    private static long getFileSize(File file) throws Exception
    {
        long size = 0;
        if (file.exists()){
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        else{
            file.createNewFile();
        }
        return size;
    }
}
