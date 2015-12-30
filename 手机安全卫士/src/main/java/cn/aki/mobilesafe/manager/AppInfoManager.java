package cn.aki.mobilesafe.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.aki.mobilesafe.bean.AppInfo;

/**
 * Created by Administrator on 2015/12/29.
 * 软件信息管理类
 */
public class AppInfoManager {

    private Context mContext;
    public AppInfoManager(Context context){
        mContext=context;
    }

    /**
     * 获得所有应用信息
     * @return  应用信息集合
     */
    public List<AppInfo> getList(){
        List<AppInfo> appInfoList=new ArrayList<>();
        PackageManager packageManager = mContext.getPackageManager();
        List<PackageInfo> installedPackageList = packageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo:installedPackageList){
            AppInfo appInfo=new AppInfo();
            //应用名
            appInfo.setApkName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            //图标
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            //包名
            appInfo.setPackageName(packageInfo.packageName);
            int flags=packageInfo.applicationInfo.flags;
            //是否安装在SD卡
            appInfo.setIsSD((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0);
            //是否为系统应用
            appInfo.setIsSystem((flags&ApplicationInfo.FLAG_SYSTEM)!=0);
            //应用大小(不准确)
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            appInfo.setSize(new File(sourceDir).length());
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }

}
