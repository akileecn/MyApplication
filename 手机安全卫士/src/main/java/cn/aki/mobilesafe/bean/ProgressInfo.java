package cn.aki.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/1/11.
 * 进程信息
 */
public class ProgressInfo {
    private Drawable icon;  //图标
    private String appName; //应用名
    private String progressName;    //进程名字
    private Long memorySize;    //占内存
    private boolean isSystem;   //是否系统应用
    private boolean checked;    //是否选中
    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getProgressName() {
        return progressName;
    }

    public void setProgressName(String progressName) {
        this.progressName = progressName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "ProgressInfo{" +
                "appName='" + appName + '\'' +
                ", progressName='" + progressName + '\'' +
                ", memorySize=" + memorySize +
                ", isSystem=" + isSystem +
                '}';
    }
}
