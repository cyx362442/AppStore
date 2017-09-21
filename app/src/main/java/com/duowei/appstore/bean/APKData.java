package com.duowei.appstore.bean;

/**
 * Created by Administrator on 2017-09-21.
 */

public class APKData {

    /**
     * versionCode : 1
     * versionName : 1.0
     * appName : 多维手持POS
     * updateTime : 2017-09-21
     * content : 多维手持POS点餐
     * updateMsg : 1、沽清单品实时刷新;
     2、转台数据实时同步到厨房屏。
     3、修复己知bug。
     * apkName : dw_pos.apk
     * apkUrl : http://ouwtfo4eg.bkt.clouddn.com/dw_pos.apk
     * imgUrl : http://owm0ww8l4.bkt.clouddn.com/dwpos.png
     */

    private String versionCode;
    private String versionName;
    private String appName;
    private String updateTime;
    private String content;
    private String updateMsg;
    private String apkName;
    private String apkUrl;
    private String imgUrl;
    private boolean isLoad=false;
    private int pro=0;

    public int getPro() {
        return pro;
    }

    public void setPro(int pro) {
        this.pro = pro;
    }

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
