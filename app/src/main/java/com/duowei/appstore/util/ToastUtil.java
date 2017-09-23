package com.duowei.appstore.util;

import android.widget.Toast;

import com.duowei.appstore.app.MyApp;

/**
 * Created by Administrator on 2017-09-23.
 */

public class ToastUtil {
    private static Toast mToast = null;

    private static long lastClickTime;

    // 防止连续点击按钮
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1900) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    static {
        mToast = Toast.makeText(MyApp.getContext(), "",
                Toast.LENGTH_SHORT);
    }

    public static void showToast(String str) {
        try {
            mToast.setText(str);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
