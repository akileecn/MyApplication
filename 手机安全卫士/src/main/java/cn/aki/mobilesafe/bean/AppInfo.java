package cn.aki.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/12/28.
 * 软件信息
 */
public class AppInfo {
    private Drawable icon;  //图标
    private String apkName; //应用名
    private String packageName; //包名
    private Long size;  //大小
    private Boolean isSystem;   //是否系统应用
    private Boolean isSD;  //是否

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getIsSD() {
        return isSD;
    }

    public void setIsSD(Boolean isSD) {
        this.isSD = isSD;
    }

    public String getLocation(){
        if(isSD){
            return "SD卡";
        }else{
            return "手机内存";
        }
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "apkName='" + apkName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", size=" + size +
                ", isSystem=" + isSystem +
                ", isSD=" + isSD +
                '}';
    }
}
